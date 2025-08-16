import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

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
}

interface InterviewRoomActions {
  setStatus: (
    status: (typeof interviewStatus)[keyof typeof interviewStatus],
  ) => void;
  setRole: (role: (typeof interviewRole)[keyof typeof interviewRole]) => void;
  setType: (type: (typeof interviewType)[keyof typeof interviewType]) => void;
}

const initialState: InterviewRoomState = {
  status: interviewStatus.BEFORE_INTERVIEW,
  role: interviewRole.INTERVIEWEE,
  type: interviewType.SOLO,
};

const useInterviewRoomStore = create<
  InterviewRoomState & InterviewRoomActions
>()(
  persist(
    set => ({
      ...initialState,
      setStatus: status => set({ status }),
      setRole: role => set({ role }),
      setType: type => set({ type }),
    }),
    {
      name: 'interviewRoomStore',
      storage: createJSONStorage(() => localStorage),
      partialize: state => {
        const { status, role, type } = state;
        return { status, role, type };
      },
      version: 1,
    },
  ),
);

export default useInterviewRoomStore;
