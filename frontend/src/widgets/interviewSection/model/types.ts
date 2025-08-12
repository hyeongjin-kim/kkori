export type MediaStreamType = 'my' | 'peer';

export interface NextQuestionButtonProps {
  nextQuestion: string;
  label: string;
  onClick: () => void;
}
