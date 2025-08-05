import {
  PRACTICE_MODE,
  PracticeButton,
} from '@/pages/homePage/ui/PracticeButton';

function SoloPracticeButton() {
  return (
    <PracticeButton
      text="혼자 연습하기"
      path="/solo-practice"
      className="bg-point-500 text-text-black"
      mode={PRACTICE_MODE.SOLO_PRACTICE}
    />
  );
}

export default SoloPracticeButton;
