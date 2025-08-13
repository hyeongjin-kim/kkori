import { PracticeButton } from '@/pages/homePage/ui/PracticeButton';

interface SoloPracticeButtonProps {
  onClick: () => void;
}

function SoloPracticeButton({ onClick }: SoloPracticeButtonProps) {
  return (
    <PracticeButton
      text="혼자 연습하기"
      onClick={onClick}
      className="bg-point-500 text-text-black"
    />
  );
}

export default SoloPracticeButton;
