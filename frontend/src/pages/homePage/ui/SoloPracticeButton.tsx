import { PracticeButton } from '@/pages/homePage/ui/PracticeButton';
import { interviewType } from '@/entities/interviewRoom/model/useInterviewRoomStore';

function SoloPracticeButton() {
  return (
    <PracticeButton
      text="혼자 연습하기"
      path="/solo-practice"
      className="bg-point-500 text-text-black"
      mode={interviewType.SOLO}
    />
  );
}

export default SoloPracticeButton;
