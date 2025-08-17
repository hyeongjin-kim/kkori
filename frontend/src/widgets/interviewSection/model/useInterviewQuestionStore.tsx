import { mockupQuestion } from '@/__mocks__/questionMocks';
import { create } from 'zustand';

export interface Question {
  id: number;
  question: string;
  questionType: string;
}

interface InterviewQuestionState {
  currentQuestion: Question;
  tailQuestion: Question[];
  defaultQuestion: Question;
  nextQuestion: Question;
  customQuestion: string;
}

interface InterviewQuestionActions {
  setCurrentQuestion: (question: Question) => void;
  setTailQuestion: (questions: Question[]) => void;
  setDefaultQuestion: (question: Question) => void;
  setNextQuestion: (question: Question) => void;
  setCustomQuestion: (question: string) => void;
  clearCurrentQuestion: () => void;
}

type InterviewQuestionStore = InterviewQuestionState & InterviewQuestionActions;

const initialState: InterviewQuestionState = {
  currentQuestion: mockupQuestion.currentQuestion,
  tailQuestion: [mockupQuestion.tailQuestion, mockupQuestion.tailQuestion],
  defaultQuestion: mockupQuestion.defaultQuestion,
  nextQuestion: mockupQuestion.nextQuestion,
  customQuestion: '직접 질문 만들기',
};

export const useInterviewQuestionStore = create<InterviewQuestionStore>(
  set => ({
    ...initialState,
    setCurrentQuestion: question => set({ currentQuestion: question }),
    setTailQuestion: questions => set({ tailQuestion: questions }),
    setDefaultQuestion: question => set({ defaultQuestion: question }),
    setNextQuestion: question => set({ nextQuestion: question }),
    setCustomQuestion: question => set({ customQuestion: question }),
    clearCurrentQuestion: () =>
      set({ currentQuestion: mockupQuestion.currentQuestion }),
  }),
);
