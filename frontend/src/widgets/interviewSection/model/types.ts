import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';

export type MediaStreamType = 'my' | 'peer';

export interface NextQuestionButtonProps {
  nextQuestion: string;
  label: string;
  onClick: () => void;
}

export const controlStatus = {
  ...interviewStatus,
  ALWAYS: 'always',
  DURING_INTERVIEW: 'duringInterview',
  END_INTERVIEW: 'endInterview',
};
