export const CHAT_TYPES = {
  question: 'question',
  answer: 'answer',
  chat: 'chat',
} as const;

export interface NameTaggedChatMessageProps {
  message: Message;
}

export interface Message {
  id?: string;
  type: (typeof CHAT_TYPES)[keyof typeof CHAT_TYPES];
  sender: string;
  text: string;
  timestamp: string;
  isMyMessage?: boolean;
  confirmed: boolean;
}
