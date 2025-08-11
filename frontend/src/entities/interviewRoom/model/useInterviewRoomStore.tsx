import { create } from 'zustand';

export const interviewStatus = {
  BEFORE_INTERVIEW: 'beforeInterview',
  QUESTION_PRESENTED: 'questionPresented',
  ANSWER_START: 'answerStart',
  ANSWER_END: 'answerEnd',
  NEXT_QUESTION_SELECTED: 'nextQuestionSelected',
  CUSTOM_QUESTION_START: 'customQuestionStart',
  CUSTOM_QUESTION_CREATED: 'customQuestionCreated',
  END_INTERVIEW: 'endInterview',
};

interface InterviewRoomState {
  status: (typeof interviewStatus)[keyof typeof interviewStatus];
  role: 'interviewee' | 'interviewer';
  interviewType: 'solo' | 'pair';
}

interface InterviewRoomActions {
  setStatus: (
    status: (typeof interviewStatus)[keyof typeof interviewStatus],
  ) => void;
  setRole: (role: 'interviewee' | 'interviewer') => void;
  setInterviewType: (interviewType: 'solo' | 'pair') => void;
}

const initialState: InterviewRoomState = {
  status: interviewStatus.BEFORE_INTERVIEW,
  role: 'interviewee',
  interviewType: 'solo',
};

const useInterviewRoomStore = create<InterviewRoomState & InterviewRoomActions>(
  set => ({
    ...initialState,
    setStatus: status => set({ status }),
    setRole: role => set({ role }),
    setInterviewType: interviewType => set({ interviewType }),
  }),
);

export default useInterviewRoomStore;
