import { create } from 'zustand';

export interface Question {
  id: number;
  question: string;
}

interface CurrentQuestionState {
  question: Question;
}

interface CurrentQuestionActions {
  setQuestion: (question: Question) => void;
}

type CurrentQuestionStore = CurrentQuestionState & CurrentQuestionActions;

const initialState: CurrentQuestionState = {
  question: {
    id: 0,
    question: '명령형 프로그래밍과 선언형 프로그래밍이 무엇인가요?',
  },
};

export const useCurrentQuestionStore = create<CurrentQuestionStore>(set => ({
  ...initialState,
  setQuestion: question => set({ question }),
}));
