import ControlButton from '@/components/practicePage/ControlButton';
import {
  startInterview,
  endInterview,
} from '@/components/practicePage/controlButtonFunction';

function PreInterviewControlButtonContainer() {
  return (
    <div
      aria-label="pre-interview-control-button-container"
      className="flex w-full items-center justify-center gap-4"
    >
      <ControlButton
        onClick={startInterview}
        label="interview-start"
        text="면접 시작"
      />
      <ControlButton
        onClick={endInterview}
        label="interview-end"
        text="나가기"
      />
    </div>
  );
}

export default PreInterviewControlButtonContainer;
