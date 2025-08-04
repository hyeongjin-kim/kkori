import { mockupQuestion } from '@/__mocks__/questionMocks';
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
  question: mockupQuestion,
};

export const useCurrentQuestionStore = create<CurrentQuestionStore>(set => ({
  ...initialState,
  setQuestion: question => set({ question }),
}));
