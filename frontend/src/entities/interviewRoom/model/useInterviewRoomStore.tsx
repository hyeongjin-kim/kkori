import { create } from 'zustand';

export const interviewStatus = {
  BEFORE_INTERVIEW: 'beforeInterview',
  QUESTION_PRESENTED: 'questionPresented',
  ANSWER_START: 'answerStart',
  ANSWER_END: 'answerEnd',
  NEXT_QUESTIONS_PRESENTED: 'nextQuestionsPresented',
  NEXT_QUESTION_SELECTED: 'nextQuestionSelected',
  CUSTOM_QUESTION_START: 'customQuestionStart',
  CUSTOM_QUESTION_CREATED: 'customQuestionCreated',
  END_INTERVIEW: 'endInterview',
};

interface InterviewRoomState {
  status: keyof typeof interviewStatus;
}

interface InterviewRoomActions {
  setStatus: (status: keyof typeof interviewStatus) => void;
}

const initialState: InterviewRoomState = {
  status: 'BEFORE_INTERVIEW',
};

const useInterviewRoomStore = create<InterviewRoomState & InterviewRoomActions>(
  set => ({
    ...initialState,
    setStatus: status => set({ status }),
  }),
);

export default useInterviewRoomStore;
