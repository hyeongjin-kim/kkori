import { PracticeButton } from '@/pages/homePage/ui/PracticeButton';
import { interviewType } from '@/entities/interviewRoom/model/useInterviewRoomStore';

function PairPracticeButton() {
  return (
    <PracticeButton
      text="같이 연습하기"
      path="/pair-practice"
      className="border-point-300 text-point-300 border"
      mode={interviewType.PAIR}
    />
  );
}

export default PairPracticeButton;
