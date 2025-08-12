import { create } from 'zustand';

export const interviewStatus = {
  BEFORE_INTERVIEW: 'beforeInterview',
  QUESTION_PRESENTED: 'questionPresented',
  ANSWER_START: 'answerStart',
  ANSWER_SUBMIT: 'answerSubmit',
  NEXT_QUESTION_PRESENTED: 'nextQuestionPresented',
  NEXT_QUESTION_SELECTED: 'nextQuestionSelected',
  CUSTOM_QUESTION_START: 'customQuestionStart',
  CUSTOM_QUESTION_CREATED: 'customQuestionCreated',
  END_INTERVIEW: 'endInterview',
  ALWAYS: 'always',
};

export const interviewType = Object.freeze({
  SOLO: 'solo',
  PAIR: 'pair',
});

interface InterviewRoomState {
  status: (typeof interviewStatus)[keyof typeof interviewStatus];
  role: 'interviewee' | 'interviewer';
  type: (typeof interviewType)[keyof typeof interviewType];
  modalOpen: boolean;
}

interface InterviewRoomActions {
  setStatus: (
    status: (typeof interviewStatus)[keyof typeof interviewStatus],
  ) => void;
  setRole: (role: 'interviewee' | 'interviewer') => void;
  setType: (type: (typeof interviewType)[keyof typeof interviewType]) => void;
  setModalOpen: (modalOpen: boolean) => void;
}

const initialState: InterviewRoomState = {
  status: interviewStatus.BEFORE_INTERVIEW,
  role: 'interviewee',
  type: interviewType.SOLO,
  modalOpen: false,
};

const useInterviewRoomStore = create<InterviewRoomState & InterviewRoomActions>(
  set => ({
    ...initialState,
    setStatus: status => set({ status }),
    setRole: role => set({ role }),
    setType: type => set({ type }),
    setModalOpen: modalOpen => set({ modalOpen }),
  }),
);

export default useInterviewRoomStore;
