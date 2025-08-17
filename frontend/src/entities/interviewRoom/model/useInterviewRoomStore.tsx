import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';
import useInterviewToastStore from '@/widgets/interviewSection/model/useInterviewToastStore';

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

const statusToToastMessage = {
  [interviewStatus.BEFORE_INTERVIEW]: '면접 시작 전 상태로 변경되었습니다.',
  [interviewStatus.QUESTION_PRESENTED]: '질문 제시 되었습니다.',
  [interviewStatus.ANSWER_START]: '답변 녹음을 시작했습니다.',
  [interviewStatus.ANSWER_SUBMIT]:
    '답변이 제출 되었습니다. 다음 질문을 준비합니다.',
  [interviewStatus.NEXT_QUESTION_PRESENTED]: '다음 질문이 제시 되었습니다.',
  [interviewStatus.NEXT_QUESTION_SELECTED]: '다음 질문이 선택 되었습니다.',
  [interviewStatus.CUSTOM_QUESTION_SELECTED]: '커스텀 질문이 선택 되었습니다.',
  [interviewStatus.CUSTOM_QUESTION_START]:
    '커스텀 질문 녹음이 시작 되었습니다.',
  [interviewStatus.CUSTOM_QUESTION_CREATED]: '커스텀 질문이 생성 되었습니다.',
  [interviewStatus.END_INTERVIEW]: '면접이 종료 되었습니다.',
};

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
      setStatus: status => {
        set({ status });
        useInterviewToastStore
          .getState()
          .addToast(statusToToastMessage[status]);
      },
      setRole: role => {
        set({ role });
        useInterviewToastStore
          .getState()
          .addToast(
            role === interviewRole.INTERVIEWER
              ? '역할이 면접관으로 변경되었습니다.'
              : '역할이 면접자로 변경되었습니다.',
          );
      },
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
