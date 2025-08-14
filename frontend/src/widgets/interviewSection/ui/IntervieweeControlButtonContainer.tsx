import ControlButton from '@/widgets/interviewSection/ui/ControlButton';
import {
  pairIntervieweeControlButtonProps,
  soloIntervieweeControlButtonProps,
} from '@/widgets/interviewSection/model/constants';
import useInterviewRoomStore, {
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';

function IntervieweeControlButtonContainer() {
  const controlButtonProps = useInterviewRoomStore(state =>
    state.type === interviewType.SOLO
      ? soloIntervieweeControlButtonProps
      : pairIntervieweeControlButtonProps,
  );
  return (
    <div
      aria-label="interviewee-control-button-container"
      className="flex gap-4"
    >
      {controlButtonProps.map(({ label, onClick, text, status, path }) => (
        <ControlButton
          key={label}
          label={label}
          onClick={onClick}
          text={text}
          status={status}
          path={path}
        />
      ))}
    </div>
  );
}

export default IntervieweeControlButtonContainer;
