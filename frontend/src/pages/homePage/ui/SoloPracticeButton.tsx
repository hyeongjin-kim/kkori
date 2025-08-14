import useInterviewRoomStore, {
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { PracticeButton } from '@/pages/homePage/ui/PracticeButton';

interface SoloPracticeButtonProps {
  onClick: () => void;
}

function SoloPracticeButton({ onClick }: SoloPracticeButtonProps) {
  const handleClick = () => {
    useInterviewRoomStore.getState().setType(interviewType.SOLO);
    onClick();
  };
  return (
    <PracticeButton
      text="혼자 연습하기"
      onClick={handleClick}
      className="bg-point-500 text-text-black"
    />
  );
}

export default SoloPracticeButton;
