import { create } from 'zustand';

export const interviewStatus = {
  BEFORE_INTERVIEW: 'beforeInterview',
  QUESTION_PRESENTED: 'questionPresented',
  ANSWER_START: 'answerStart',
  ANSWER_END: 'answerEnd',
  ANSWER_SUBMIT: 'answerSubmit',
  NEXT_QUESTION_CHOICE: 'nextQuestionChoice',
  NEXT_QUESTION_SELECTED: 'nextQuestionSelected',
  CUSTOM_QUESTION_START: 'customQuestionStart',
  CUSTOM_QUESTION_CREATED: 'customQuestionCreated',
  END_INTERVIEW: 'endInterview',
  ALWAYS: 'always',
};

interface InterviewRoomState {
  status: (typeof interviewStatus)[keyof typeof interviewStatus];
  role: 'interviewee' | 'interviewer';
  interviewType: 'solo' | 'pair';
  modalOpen: boolean;
}

interface InterviewRoomActions {
  setStatus: (
    status: (typeof interviewStatus)[keyof typeof interviewStatus],
  ) => void;
  setRole: (role: 'interviewee' | 'interviewer') => void;
  setInterviewType: (interviewType: 'solo' | 'pair') => void;
  setModalOpen: (modalOpen: boolean) => void;
}

const initialState: InterviewRoomState = {
  status: interviewStatus.BEFORE_INTERVIEW,
  role: 'interviewee',
  interviewType: 'solo',
  modalOpen: false,
};

const useInterviewRoomStore = create<InterviewRoomState & InterviewRoomActions>(
  set => ({
    ...initialState,
    setStatus: status => set({ status }),
    setRole: role => set({ role }),
    setInterviewType: interviewType => set({ interviewType }),
    setModalOpen: modalOpen => set({ modalOpen }),
  }),
);

export default useInterviewRoomStore;
