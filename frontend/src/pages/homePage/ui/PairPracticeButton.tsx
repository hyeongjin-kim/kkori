import useInterviewRoomStore, {
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { PracticeButton } from '@/pages/homePage/ui/PracticeButton';

interface PairPracticeButtonProps {
  onClick: () => void;
}

function PairPracticeButton({ onClick }: PairPracticeButtonProps) {
  const handleClick = () => {
    useInterviewRoomStore.getState().setType(interviewType.PAIR);
    onClick();
  };
  return (
    <PracticeButton
      text="같이 연습하기"
      onClick={handleClick}
      className="border-point-300 text-point-300 border"
    />
  );
}

export default PairPracticeButton;
