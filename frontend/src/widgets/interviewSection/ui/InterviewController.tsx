import usePracticeStore from '@/stores/usePracticeStore';
import { PracticeType } from '@/stores/usePracticeStore';
import {
  interviewControlButtonProps,
  preInterviewControlButtonProps,
} from '@/widgets/interviewSection/model/constants';
import ControlButton from '@/widgets/interviewSection/ui/ControlButton';

const switchStatus = (status: PracticeType) => {
  switch (status) {
    case 'pre-interview':
      return preInterviewControlButtonProps;
    case 'interview':
      return interviewControlButtonProps;
  }
};

function InterviewController() {
  const status = usePracticeStore(state => state.status);

  return (
    <div
      aria-label="interview-controller"
      className="flex w-full items-center justify-center p-8"
    >
      <div
        aria-label="pre-interview-control-button-container"
        className="flex gap-4"
      >
        {switchStatus(status).map(({ onClick, label, text }) => (
          <ControlButton
            key={label}
            onClick={onClick}
            label={label}
            text={text}
          />
        ))}
      </div>
    </div>
  );
}

export default InterviewController;
