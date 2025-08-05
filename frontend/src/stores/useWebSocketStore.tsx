import { create } from 'zustand';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { RoomCreateRequest } from '@/customTypes/practicePage/RoomCreateRequest';

interface WebSocketState {
  client: Client | null;
  isConnected: boolean;
  roomID: string | null;
}

interface WebSocketAction {
  connect: () => void;
  disconnect: () => void;
  roomCreate: (request: RoomCreateRequest) => void;
  interviewStart: (roomID: string) => void;
  interviewEnd: (roomID: string) => void;
  roomJoin: (roomID: string) => void;
  roomExit: (roomID: string) => void;
  answerStart: (roomID: string, questionID: string) => void;
  answerSubmit: (roomID: string, questionID: string) => void;
  nextQuestionSelect: (roomID: string) => void;
  customQuestionStart: (roomID: string) => void;
  customQuestionCreate: (roomID: string, questionText: string) => void;
}

const initialState: WebSocketState = {
  client: null,
  isConnected: false,
  roomID: null,
};

export const useWebSocketStore = create<WebSocketState & WebSocketAction>(
  (set, get) => ({
    ...initialState,
    connect: () => {
      if (get().client || get().isConnected) return;
      const client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
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
            personalMessageHandler(client, set, response);
          });
          get().roomCreate({ mode: 'SOLO_PRACTICE', questionSetId: 1 });
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
    interviewStart: (roomID: string) => {
      get().client?.publish({
        destination: `/app/interview-start`,
        body: JSON.stringify({ roomId: roomID }),
      });
    },
    interviewEnd: (roomID: string) => {
      get().client?.publish({
        destination: `/app/interview-end`,
        body: JSON.stringify({ roomId: roomID }),
      });
    },
    roomJoin: (roomID: string) => {
      get().client?.publish({
        destination: `/app/room-join`,
        body: JSON.stringify({ roomId: roomID }),
      });
    },
    roomExit: (roomID: string) => {
      get().client?.publish({
        destination: `/app/room-exit`,
        body: JSON.stringify({ roomId: roomID }),
      });
    },
    answerStart: (roomID: string) => {
      get().client?.publish({
        destination: `/app/answer-start`,
        body: JSON.stringify({ roomId: roomID }),
      });
    },
    answerSubmit: (roomID: string) => {
      get().client?.publish({
        destination: `/app/answer-submit`,
        body: JSON.stringify({
          roomId: roomID,
          //TODO: 오디오 파일 같이 전달해야함
        }),
      });
    },
    nextQuestionSelect: (roomID: string) => {
      get().client?.publish({
        destination: `/app/next-question-select`,
        body: JSON.stringify({
          roomId: roomID,
          questionType: 'DEFAULT',
          questionId: '1',
          questionText: 'test',
        }),
      });
    },
    customQuestionStart: (roomID: string) => {
      get().client?.publish({
        destination: `/app/custom-question-start`,
        body: JSON.stringify({ roomId: roomID }),
      });
    },
    customQuestionCreate: (roomID: string) => {
      get().client?.publish({
        destination: `/app/custom-question-create`,
        body: JSON.stringify({
          roomId: roomID,
          //TODO: 커스텀 질문 오디오 파일 전달해야 함
        }),
      });
    },
  }),
);

const personalMessageHandler = (client: Client, set: any, response: any) => {
  switch (response.type) {
    case 'room-created':
      roomCreatedHandler(client, set, response.data);
      break;
    case 'existing-user':
      existingUserHandler(client, set, response.data);
      //TODO: offer 전송
      break;
    case 'joined-user':
      joinedUserHandler(client, set, response.data);

      break;
    case 'room-status':
      roomStatusHandler(client, set, response.data);
      break;
    case 'offer':
      //RTC 요청
      offerHandler(client, set, response.data);
      break;
    case 'answer':
      //RTC 응답
      answerHandler(client, set, response.data);
      break;
    case 'error':
      errorHandler(client, set, response.data);
      break;
  }
};

const roomCreatedHandler = (client: Client, set: any, response: any) => {
  set({ roomID: response.roomId });
  client.subscribe(`/topic/interview/${response.roomId}`, message => {
    const response = JSON.parse(message.body);
    InterviewMessageHandler(client, set, response);
  });
};

const existingUserHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const joinedUserHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const roomStatusHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const offerHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const answerHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const InterviewMessageHandler = (client: Client, set: any, response: any) => {
  switch (response.type) {
    case 'user-exited':
      userExitedHandler(client, set, response.data);
      break;
    case 'interview-started':
      interviewStartedHandler(client, set, response.data);
      break;
    case 'interview-ended':
      interviewEndedHandler(client, set, response.data);
      break;
    case 'answer-recording-started':
      answerRecordingStartHandler(client, set, response.data);
      break;
    case 'stt-result':
      sttResultHandler(client, set, response.data);
      break;
    case 'next-question-choices':
      nextQuestionChoiceHandler(client, set, response.data);
      break;
    case 'next-question-selected':
      nextQuestionSelectedHandler(client, set, response.data);
      break;
    case 'custom-question-recording-started':
      customQuestionStartHandler(client, set, response.data);
      break;
    case 'custom-question-created':
      customQuestionCreatedHandler(client, set, response.data);
      break;
    case 'error':
      errorHandler(client, set, response.data);
      break;
  }
};

const userExitedHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const interviewStartedHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const interviewEndedHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const answerRecordingStartHandler = (
  client: Client,
  set: any,
  response: any,
) => {
  console.log(response);
};

const sttResultHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const nextQuestionChoiceHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};

const nextQuestionSelectedHandler = (
  client: Client,
  set: any,
  response: any,
) => {
  console.log(response);
};

const customQuestionStartHandler = (
  client: Client,
  set: any,
  response: any,
) => {
  console.log(response);
};

const customQuestionCreatedHandler = (
  client: Client,
  set: any,
  response: any,
) => {
  console.log(response);
};

const errorHandler = (client: Client, set: any, response: any) => {
  console.log(response);
};
