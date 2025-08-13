import { StateCreator } from 'zustand';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { InterviewSlice } from '@/widgets/interviewSection/model/interviewSlice';
import { ChattingWindowSlice } from '@/widgets/chattingWindow/model/chattingWindowSlice';
import useInterviewRoomStore, {
  interviewStatus,
  interviewType,
  interviewRole,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { audioPost } from '../api/api';
import {
  Question,
  useInterviewQuestionStore,
} from '@/widgets/interviewSection/model/useInterviewQuestionStore';
import { soloWebSocketEventHandler } from '@/shared/lib/soloWebSocketEventHandler';
import { pairWebSocketEventHandler } from '@/shared/lib/pairWebSocketEventHandler';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';

interface RoomCreateRequest {
  mode: string;
  questionSetId: number;
}

export const JOIN_ROOM_MODE = {
  CREATE_ROOM: 'CREATE_ROOM',
  JOIN_ROOM: 'JOIN_ROOM',
} as const;

interface WebSocketState {
  client: Client | null;
  isConnected: boolean;
  roomId: string | null;
  questionSetId: number;
  opponentNickname: string;
  peerConnection: RTCPeerConnection | null;
  joinRoomMode: (typeof JOIN_ROOM_MODE)[keyof typeof JOIN_ROOM_MODE];
}
type Store = ChattingWindowSlice & WebSocketSlice & InterviewSlice;

interface WebSocketAction {
  connect: () => void;
  disconnect: () => void;
  roomCreate: (request: RoomCreateRequest) => void;
  interviewStart: () => void;
  interviewEnd: () => void;
  roomJoin: () => void;
  sendMessage: (sender: string, content: string, timestamp: number) => void;
  roomExit: () => void;
  answerStart: () => void;
  answerSubmit: () => void;
  nextQuestionSelect: () => void;
  customQuestionStart: () => void;
  customQuestionCreate: () => void;
  setPeerConnection: (peerConnection: RTCPeerConnection) => void;
  setQuestionSetId: (questionSetId: number) => void;
  setRoomId: (roomId: string) => void;
  setJoinRoomMode: (
    joinRoomMode: (typeof JOIN_ROOM_MODE)[keyof typeof JOIN_ROOM_MODE],
  ) => void;
  setOpponentNickname: (opponentNickname: string) => void;
}

export interface WebSocketSlice extends WebSocketState, WebSocketAction {}

const initialState: WebSocketState = {
  client: null,
  isConnected: false,
  roomId: null,
  questionSetId: 0,
  opponentNickname: '',
  peerConnection: null,
  joinRoomMode: JOIN_ROOM_MODE.CREATE_ROOM,
};

export const createWebSocketSlice: StateCreator<
  Store,
  [],
  [],
  WebSocketSlice
> = (set, get) => ({
  ...initialState,
  connect: () => {
    if (get().client || get().isConnected) return;
    const type = useInterviewRoomStore.getState().type;
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
          if (type === interviewType.SOLO) {
            soloWebSocketEventHandler(client, get, set, response);
          } else {
            pairWebSocketEventHandler(client, get, set, response);
          }
        });
        if (get().joinRoomMode === JOIN_ROOM_MODE.CREATE_ROOM) {
          useInterviewRoomStore.getState().setRole(interviewRole.INTERVIEWEE);
          get().roomCreate({
            mode: useInterviewRoomStore.getState().type,
            questionSetId: get().questionSetId,
          });
        } else {
          useInterviewRoomStore.getState().setRole(interviewRole.INTERVIEWER);
          get().roomJoin();
        }
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
  setRoomId: (roomId: string) => {
    console.log('setRoomId', roomId);
    set({ roomId });
  },
  interviewStart: () => {
    get().client?.publish({
      destination: `/app/interview-start`,
      body: JSON.stringify({ roomId: get().roomId }),
    });
  },
  interviewEnd: () => {
    useInterviewRoomStore.getState().setStatus('endInterview');
    get().client?.publish({
      destination: `/app/interview-end`,
      body: JSON.stringify({ roomId: get().roomId }),
    });
  },
  roomJoin: () => {
    get().client?.publish({
      destination: `/app/room-join`,
      body: JSON.stringify({ roomId: get().roomId }),
    });
  },
  roomExit: () => {
    get().client?.publish({
      destination: `/app/room-exit`,
      body: JSON.stringify({ roomId: get().roomId }),
    });
  },
  sendMessage: (sender: string, content: string, timestamp: number) => {
    get().client?.publish({
      destination: `/app/chat`,
      body: JSON.stringify({
        roomId: get().roomId,
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
      body: JSON.stringify({ roomId: get().roomId }),
    });
  },
  answerSubmit: async () => {
    const blob = useMediaStreamStore.getState().blob;
    if (!blob) return;
    useInterviewRoomStore.getState().setStatus(interviewStatus.ANSWER_SUBMIT);
    await audioPost({
      url: '/api/interview/answer-submit',
      roomId: get().roomId || '',
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
        roomId: get().roomId,
        questionType: nextQuestion.questionType,
        questionId: nextQuestion.id,
        questionText: nextQuestion.question,
      }),
    });
  },
  customQuestionStart: () => {
    useInterviewRoomStore.getState().setStatus('customQuestionStart');
    get().client?.publish({
      destination: `/app/custom-question-start`,
      body: JSON.stringify({ roomId: get().roomId }),
    });
  },
  customQuestionCreate: async () => {
    const blob = useMediaStreamStore.getState().blob;
    if (!blob) return;
    useInterviewRoomStore
      .getState()
      .setStatus(interviewStatus.CUSTOM_QUESTION_CREATED);
    await audioPost({
      url: '/api/interview/custom-question-create',
      roomId: get().roomId || '',
      audioFile: blob,
    });
  },
  setPeerConnection: (peerConnection: RTCPeerConnection) => {
    set({ peerConnection });
  },
  setQuestionSetId: (questionSetId: number) => {
    set({ questionSetId });
  },
  setJoinRoomMode: (
    joinRoomMode: (typeof JOIN_ROOM_MODE)[keyof typeof JOIN_ROOM_MODE],
  ) => {
    set({ joinRoomMode });
  },
  setOpponentNickname: (opponentNickname: string) => {
    set({ opponentNickname });
  },
});
