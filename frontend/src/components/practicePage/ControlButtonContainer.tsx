import ControlButton from '@/components/practicePage/ControlButton';
import {
  switchScreen,
  startAnswer,
  endAnswer,
} from '@/components/practicePage/controlButtonfunction';

function ControlButtonContainer() {
  return (
    <div
      aria-label="control-button-container"
      className="flex w-full items-center gap-4"
    >
      <ControlButton
        onClick={switchScreen}
        label="screen-change"
        text="화면 전환"
      />
      <ControlButton
        onClick={startAnswer}
        label="answer-start"
        text="답변 시작"
      />
      <ControlButton onClick={endAnswer} label="answer-end" text="답변 종료" />
    </div>
  );
}

export default ControlButtonContainer;
