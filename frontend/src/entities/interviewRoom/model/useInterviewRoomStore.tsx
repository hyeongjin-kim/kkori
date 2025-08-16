import { create } from 'zustand';

export const interviewStatus = {
  BEFORE_INTERVIEW: 'beforeInterview',
  QUESTION_PRESENTED: 'questionPresented',
  ANSWER_START: 'answerStart',
  ANSWER_SUBMIT: 'answerSubmit',
  NEXT_QUESTION_PRESENTED: 'nextQuestionPresented',
  NEXT_QUESTION_SELECTED: 'nextQuestionSelected',
  CUSTOM_QUESTION_SELECTED: 'customQuestionSelected',
  CUSTOM_QUESTION_START: 'customQuestionStart',
  CUSTOM_QUESTION_CREATED: 'customQuestionCreated',
  END_INTERVIEW: 'endInterview',
};

export type InterviewStatus =
  (typeof interviewStatus)[keyof typeof interviewStatus];

export const interviewType = Object.freeze({
  SOLO: 'SOLO_PRACTICE',
  PAIR: 'PAIR_INTERVIEW',
});

export const interviewRole = Object.freeze({
  INTERVIEWER: 'interviewer',
  INTERVIEWEE: 'interviewee',
});

interface InterviewRoomState {
  status: (typeof interviewStatus)[keyof typeof interviewStatus];
  role: (typeof interviewRole)[keyof typeof interviewRole];
  type: (typeof interviewType)[keyof typeof interviewType];
  modalOpen: boolean;
}

interface InterviewRoomActions {
  setStatus: (
    status: (typeof interviewStatus)[keyof typeof interviewStatus],
  ) => void;
  setRole: (role: (typeof interviewRole)[keyof typeof interviewRole]) => void;
  setType: (type: (typeof interviewType)[keyof typeof interviewType]) => void;
  setModalOpen: (modalOpen: boolean) => void;
}

const initialState: InterviewRoomState = {
  status: interviewStatus.BEFORE_INTERVIEW,
  role: interviewRole.INTERVIEWEE,
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
