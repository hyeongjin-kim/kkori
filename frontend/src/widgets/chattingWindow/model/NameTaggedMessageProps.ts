export const CHAT_TYPES = {
  QUESTION: 'question',
  ANSWER: 'answer',
  OPPONENT: 'opponent',
  USER: 'user',
} as const;

export interface NameTaggedMessageProps {
  id: string;
  type: (typeof CHAT_TYPES)[keyof typeof CHAT_TYPES];
  sender: string;
  message: string;
}
