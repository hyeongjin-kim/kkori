import { mockMessageExamples } from '@/__mocks__/chatMocks';
import { NameTaggedMessageProps } from '@/customTypes/practicePage/NameTaggedMessageProps';
import { create } from 'zustand';

interface ChattingWindowState {
  messages: NameTaggedMessageProps[];
}

interface ChattingWindowAction {
  addMessage: (message: NameTaggedMessageProps) => void;
  deleteMessage: (id: string) => void;
}

const useChattingWindowStore = create<
  ChattingWindowState & ChattingWindowAction
>(set => ({
  messages: mockMessageExamples,
  addMessage: message =>
    set(state => ({ messages: [...state.messages, message] })),
  deleteMessage: id =>
    set(state => ({
      messages: state.messages.filter(message => message.id !== id),
    })),
}));

export default useChattingWindowStore;
