import ControlButton from '@/widgets/interviewSection/ui/ControlButton';
import { pairInterviewerControlButtonProps } from '@/widgets/interviewSection/model/constants';

function InterviewerControlButtonContainer() {
  const controlButtonProps = pairInterviewerControlButtonProps;
  return (
    <div
      aria-label="interviewer-control-button-container"
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

export default InterviewerControlButtonContainer;
