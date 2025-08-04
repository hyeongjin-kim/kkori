import ControlButton from '@/components/practicePage/ControlButton';
import {
  switchScreen,
  startAnswer,
  endAnswer,
  startInterview,
  endInterview,
} from '@/components/practicePage/controlButtonFunction';

function InterviewControlButtonContainer() {
  return (
    <div
      aria-label="control-button-container"
      className="flex w-full items-center justify-center gap-4"
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
      <ControlButton
        onClick={endInterview}
        label="interview-end"
        text="나가기  "
      />
    </div>
  );
}

export default InterviewControlButtonContainer;
