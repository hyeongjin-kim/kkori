import { create } from 'zustand';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

interface WebSocketState {
  client: Client | null;
  isConnected: boolean;
}

interface WebSocketAction {
  connect: () => void;
  disconnect: () => void;
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
  }),
);
