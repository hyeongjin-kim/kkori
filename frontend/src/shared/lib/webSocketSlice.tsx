import { StateCreator } from 'zustand';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { InterviewSlice } from '@/widgets/interviewSection/model/interviewSlice';
import { ChattingWindowSlice } from '@/widgets/chattingWindow/model/chattingWindowSlice';
import { PRACTICE_MODE } from '@/pages/homePage/ui/PracticeButton';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';
import useInterviewRoomStore, {
  interviewStatus,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { audioPost } from '../api/api';
import {
  Question,
  useInterviewQuestionStore,
} from '@/widgets/interviewSection/model/useInterviewQuestionStore';
import { mockupQuestion } from '@/__mocks__/questionMocks';

interface RoomCreateRequest {
  mode: string;
  questionSetId: number;
}
interface WebSocketState {
  client: Client | null;
  isConnected: boolean;
  roomID: string | null;
  practiceMode: (typeof PRACTICE_MODE)[keyof typeof PRACTICE_MODE];
  questionSetId: number;
  opponentNickname: string;
}
type Store = ChattingWindowSlice & WebSocketSlice & InterviewSlice;

interface WebSocketAction {
  connect: (
    practiceMode: (typeof PRACTICE_MODE)[keyof typeof PRACTICE_MODE],
    questionSetId: number,
  ) => void;
  disconnect: () => void;
  roomCreate: (request: RoomCreateRequest) => void;
  interviewStart: () => void;
  interviewEnd: () => void;
  roomJoin: () => void;
  sendMessage: (sender: string, content: string, timestamp: number) => void;
  roomExit: () => void;
  answerStart: () => void;
  answerSubmit: (blob: Blob) => void;
  nextQuestionSelect: () => void;
  customQuestionStart: () => void;
  customQuestionCreate: (base64data: string) => void;
}

export interface WebSocketSlice extends WebSocketState, WebSocketAction {}

const initialState: WebSocketState = {
  client: null,
  isConnected: false,
  roomID: null,
  practiceMode: PRACTICE_MODE.SOLO_PRACTICE,
  questionSetId: 0,
  opponentNickname: '',
};

export const createWebSocketSlice: StateCreator<
  Store,
  [],
  [],
  WebSocketSlice
> = (set, get) => ({
  ...initialState,
  connect: (
    practiceMode: (typeof PRACTICE_MODE)[keyof typeof PRACTICE_MODE],
    questionSetId: number,
  ) => {
    if (get().client || get().isConnected) return;
    const client = new Client({
      webSocketFactory: () => new SockJS(process.env.WEBSOCKET_URL || ''),
      debug: str => {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        set({ client, isConnected: true });
        client.subscribe('/user/queue/interview', message => {
          const response = JSON.parse(message.body);
          console.log(response);
          personalMessageHandler(client, get, set, response);
        });
        get().roomCreate({ mode: practiceMode, questionSetId: questionSetId });
      },
      onDisconnect: () => {
        set({ client: null, isConnected: false });
      },
    });
    client.activate();
  },
  disconnect: () => {
    if (!get().client || !get().isConnected) return;
    get().client?.deactivate();
  },
  roomCreate: (request: RoomCreateRequest) => {
    get().client?.publish({
      destination: '/app/room-create',
      body: JSON.stringify(request),
    });
  },
  interviewStart: () => {
    get().client?.publish({
      destination: `/app/interview-start`,
      body: JSON.stringify({ roomId: get().roomID }),
    });
  },
  interviewEnd: () => {
    useInterviewRoomStore.getState().setStatus('endInterview');
    get().client?.publish({
      destination: `/app/interview-end`,
      body: JSON.stringify({ roomId: get().roomID }),
    });
  },
  roomJoin: () => {
    get().client?.publish({
      destination: `/app/room-join`,
      body: JSON.stringify({ roomId: get().roomID }),
    });
  },
  roomExit: () => {
    get().client?.publish({
      destination: `/app/room-exit`,
      body: JSON.stringify({ roomId: get().roomID }),
    });
  },
  sendMessage: (sender: string, content: string, timestamp: number) => {
    get().client?.publish({
      destination: `/app/chat`,
      body: JSON.stringify({
        roomId: get().roomID,
        senderNickname: sender,
        content: content,
        timestamp: timestamp,
      }),
    });
  },
  answerStart: () => {
    useInterviewRoomStore.getState().setStatus('answerStart');
    get().client?.publish({
      destination: `/app/answer-start`,
      body: JSON.stringify({ roomId: get().roomID }),
    });
  },
  answerSubmit: async (blob: Blob) => {
    useInterviewRoomStore.getState().setStatus(interviewStatus.ANSWER_SUBMIT);
    await audioPost({
      url: '/api/interview/answer-submit',
      roomId: get().roomID || '',
      audioFile: blob,
    });
    console.log('answerSubmit');
  },
  nextQuestionSelect: () => {
    useInterviewRoomStore.getState().setStatus('nextQuestionSelected');
    const nextQuestion = useInterviewQuestionStore.getState().nextQuestion;
    get().client?.publish({
      destination: `/app/next-question-select`,
      body: JSON.stringify({
        roomId: get().roomID,
        questionType: nextQuestion.questionType,
        questionId: nextQuestion.id,
        questionText: nextQuestion.question,
      }),
    });
  },
  customQuestionStart: () => {
    get().client?.publish({
      destination: `/app/custom-question-start`,
      body: JSON.stringify({ roomId: get().roomID }),
    });
  },
  customQuestionCreate: (base64data: string) => {
    get().client?.publish({
      destination: `/app/custom-question-create`,
      body: JSON.stringify({
        roomId: get().roomID,
        audioBase64: base64data,
      }),
    });
  },
});

const personalMessageHandler = (
  client: Client,
  get: any,
  set: any,
  response: any,
) => {
  switch (response.type) {
    case 'room-created':
      roomCreatedHandler(client, get, set, response.data);
      break;
    case 'existing-user':
      existingUserHandler(client, set, response.data);
      break;
    case 'joined-user':
      joinedUserHandler(client, set, response.data);
      break;
    case 'room-status':
      roomStatusHandler(client, set, response.data);
      break;
    case 'offer':
      offerHandler(client, set, response.data);
      break;
    case 'answer':
      answerHandler(client, set, response.data);
      break;
    case 'next-question-choices':
      nextQuestionChoiceHandler(client, set, response.data);
      break;
    default:
      errorHandler(client, set, response.data);
      break;
  }
};

const roomCreatedHandler = (
  client: Client,
  get: any,
  set: any,
  response: any,
) => {
  set({ roomID: response.roomId });
  client.subscribe(`/topic/interview/${response.roomId}`, message => {
    const response = JSON.parse(message.body);
    console.log(response);
    get().InterviewMessageHandler(client, response);
  });
};

const existingUserHandler = async (client: Client, set: any, data: any) => {
  set({ opponentNickname: data.nickname });
  const peerConnection = new RTCPeerConnection();
  const myStream = useMediaStreamStore.getState().myStream;

  if (!myStream) return;

  myStream.getTracks().forEach(track => {
    peerConnection.addTrack(track, myStream);
  });

  const offer = await peerConnection.createOffer();
  await peerConnection.setLocalDescription(offer);
  client.publish({
    destination: `/app/offer`,
    body: JSON.stringify({
      roomId: data.roomId,
      offer: offer,
    }),
  });
};

const joinedUserHandler = (client: Client, set: any, data: any) => {
  set({ opponentNickname: data.nickname });
};

const roomStatusHandler = (client: Client, set: any, data: any) => {
  console.log(data);
};

const offerHandler = async (client: Client, set: any, data: any) => {
  const peerConnection = new RTCPeerConnection({
    iceServers: [
      {
        urls: process.env.TURN_URL || '',
        username: process.env.TURN_USERNAME || '',
        credential: process.env.TURN_CREDENTIAL || '',
      },
    ],
  });
  const myStream = useMediaStreamStore.getState().myStream;
  if (!myStream) return;
  myStream.getTracks().forEach(track => {
    peerConnection.addTrack(track, myStream);
  });
  peerConnection.setRemoteDescription(data.offer);
  useMediaStreamStore.getState().setPeerStream(data.offer.stream);
  const answer = await peerConnection.createAnswer();
  await peerConnection.setLocalDescription(answer);
  client.publish({
    destination: `/app/answer`,
    body: JSON.stringify({
      roomId: data.roomId,
      answer: answer,
    }),
  });
};

const answerHandler = (client: Client, set: any, data: any) => {
  const peerConnection = new RTCPeerConnection();
  peerConnection.setRemoteDescription(data.answer);
  useMediaStreamStore.getState().setPeerStream(data.answer.stream);
};

const nextQuestionChoiceHandler = (client: Client, set: any, data: any) => {
  const questions: Question[] = [];
  data.nextQuestionChoices.forEach((choice: any) => {
    questions.push({
      question: choice.questionText,
      id: choice.questionId,
      questionType: choice.questionType,
    });
  });
  useInterviewQuestionStore
    .getState()
    .setTailQuestion([questions[0], questions[1]]);
  useInterviewQuestionStore.getState().setDefaultQuestion(questions[2]);
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.NEXT_QUESTION_PRESENTED);
  console.log(useInterviewRoomStore.getState().status);
};

const errorHandler = (client: Client, set: any, data: any) => {
  const errorMessage = data.error;
};
