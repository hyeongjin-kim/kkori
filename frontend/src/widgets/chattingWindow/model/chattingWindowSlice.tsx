import { mockMessageExamples } from '@/__mocks__/chatMocks';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { StateCreator } from 'zustand';
import { Message } from '@/widgets/chattingWindow/model/chattingWindowType';

interface ChattingWindowState {
  messages: Message[];
}

interface ChattingWindowAction {
  addMessage: (message: Message) => void;
  confirmMessage: (myMessage: Message) => void;
  deleteMessage: (myMessage: Message) => void;
}

export interface ChattingWindowSlice
  extends ChattingWindowState,
    ChattingWindowAction {}

const initialState: ChattingWindowState = {
  messages: [],
};

function checkMessage(message: Message, myMessage: Message): boolean {
  return (
    message.timestamp === myMessage.timestamp &&
    message.sender === myMessage.sender
  );
}

export const createChattingWindowSlice: StateCreator<
  ChattingWindowSlice,
  [],
  [],
  ChattingWindowSlice
> = set => ({
  ...initialState,
  addMessage: (message: Message) => {
    console.log('addMessage', message);
    set(state => ({ messages: [...state.messages, message] }));
  },
  confirmMessage: (myMessage: Message) => {
    console.log('confirmMessage', myMessage);
    set(state => ({
      messages: state.messages.map(message =>
        checkMessage(message, myMessage)
          ? { ...message, confirmed: true }
          : message,
      ),
    }));
  },
  deleteMessage: (myMessage: Message) =>
    set(state => ({
      messages: state.messages.filter(message =>
        checkMessage(message, myMessage),
      ),
    })),
});
