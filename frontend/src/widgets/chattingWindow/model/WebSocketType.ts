interface WebSocketMessage {
  id: string | null;
  sender: string;
  type: 'question' | 'answer' | 'chat';
  message: string;
  timestamp: string;
}

export type { WebSocketMessage };
