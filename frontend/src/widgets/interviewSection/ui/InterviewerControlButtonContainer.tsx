import ControlButton from '@/widgets/interviewSection/ui/ControlButton';
import { pairInterviewerControlButtonProps } from '@/widgets/interviewSection/model/constants';

function InterviewerControlButtonContainer() {
  const controlButtonProps = pairInterviewerControlButtonProps;
  return (
    <div aria-label="interviewer-control-button-container">
      {controlButtonProps.map(({ label, onClick, text, status }) => (
        <ControlButton
          key={label}
          label={label}
          onClick={onClick}
          text={text}
          status={status}
        />
      ))}
    </div>
  );
}

export default InterviewerControlButtonContainer;
