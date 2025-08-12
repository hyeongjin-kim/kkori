import ControlButton from '@/widgets/interviewSection/ui/ControlButton';
import { soloIntervieweeControlButtonProps } from '@/widgets/interviewSection/model/constants';

function IntervieweeControlButtonContainer() {
  const controlButtonProps = soloIntervieweeControlButtonProps;
  return (
    <div aria-label="interviewee-control-button-container">
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

export default IntervieweeControlButtonContainer;
