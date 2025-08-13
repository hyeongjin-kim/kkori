import { PracticeButton } from '@/pages/homePage/ui/PracticeButton';

interface PairPracticeButtonProps {
  onClick: () => void;
}

function PairPracticeButton({ onClick }: PairPracticeButtonProps) {
  return (
    <PracticeButton
      text="같이 연습하기"
      onClick={onClick}
      className="border-point-300 text-point-300 border"
    />
  );
}

export default PairPracticeButton;
