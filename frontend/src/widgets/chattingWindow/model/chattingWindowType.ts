export const CHAT_TYPES = {
  QUESTION: 'question',
  ANSWER: 'answer',
  CHAT: 'chat',
} as const;

export interface NameTaggedMessageProps {
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
