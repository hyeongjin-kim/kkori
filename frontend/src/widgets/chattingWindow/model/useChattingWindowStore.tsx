import { mockMessageExamples } from '@/__mocks__/chatMocks';
import { create } from 'zustand';
import { Message } from '@/widgets/chattingWindow/model/chattingWindowType';

interface ChattingWindowState {
  messages: Message[];
}

interface ChattingWindowAction {
  addMessage: (message: Message) => void;
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
