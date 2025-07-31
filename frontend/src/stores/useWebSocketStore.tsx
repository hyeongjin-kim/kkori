import { create } from 'zustand';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { RoomCreateRequest } from '@/customTypes/practicePage/RoomCreateRequest';

interface WebSocketState {
  client: Client | null;
  isConnected: boolean;
}

interface WebSocketAction {
  connect: () => void;
  disconnect: () => void;
  roomCreate: (request: RoomCreateRequest) => void;
  interviewStart: () => void;
  interviewEnd: () => void;
  roomJoin: () => void;
  roomExit: () => void;
  answerStart: () => void;
  answerSubmit: () => void;
  nextQuestionSelect: () => void;
  currentQuestionStart: () => void;
}

const initialState: WebSocketState = {
  client: null,
  isConnected: false,
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
            personalMessageHandler(response);
          });
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
        destination: '/app/room/create',
        body: JSON.stringify(request),
      });
    },
    interviewStart: () => {},
    interviewEnd: () => {},
    roomJoin: () => {},
    roomExit: () => {},
    answerStart: () => {},
    answerSubmit: () => {},
    nextQuestionSelect: () => {},
    currentQuestionStart: () => {},
    currentQuestionCreate: () => {},
  }),
);

const personalMessageHandler = (response: any) => {
  switch (response.type) {
    case 'room-created':
      roomCreatedHandler(response.data);
      break;
    case 'room-status':
      roomStatusHandler(response.data);
      break;
    case 'answer-processed':
      answerProcessedHandler(response.data);
      break;
    case 'error':
      errorHandler(response.data);
      break;
  }
};

const roomCreatedHandler = (response: any) => {};

const roomStatusHandler = (response: any) => {};

const answerProcessedHandler = (response: any) => {};

const errorHandler = (response: any) => {};
