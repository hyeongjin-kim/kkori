import ControlButton from './ControlButton';
import { soloIntervieweeControlButtonProps } from '../model/constants';

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
