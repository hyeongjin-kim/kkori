import { mockupQuestion } from '@/__mocks__/questionMocks';
import { create } from 'zustand';

export interface Question {
  id: number;
  question: string;
}

interface InterviewQuestionState {
  currentQuestion: Question;
  tailQuestion: Question[];
  defaultQuestion: Question;
  customQuestion: Question;
}

interface InterviewQuestionActions {
  setCurrentQuestion: (question: Question) => void;
  setTailQuestion: (questions: Question[]) => void;
  setDefaultQuestion: (question: Question) => void;
  setCustomQuestion: (question: Question) => void;
}

type InterviewQuestionStore = InterviewQuestionState & InterviewQuestionActions;

const initialState: InterviewQuestionState = {
  currentQuestion: mockupQuestion.currentQuestion,
  tailQuestion: [mockupQuestion.tailQuestion, mockupQuestion.tailQuestion],
  defaultQuestion: mockupQuestion.defaultQuestion,
  customQuestion: mockupQuestion.customQuestion,
};

export const useInterviewQuestionStore = create<InterviewQuestionStore>(
  set => ({
    ...initialState,
    setCurrentQuestion: question => set({ currentQuestion: question }),
    setTailQuestion: questions => set({ tailQuestion: questions }),
    setDefaultQuestion: question => set({ defaultQuestion: question }),
    setCustomQuestion: question => set({ customQuestion: question }),
  }),
);
