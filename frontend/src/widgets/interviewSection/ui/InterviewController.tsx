import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';
import {
  interviewControlButtonProps,
  preInterviewControlButtonProps,
} from '@/widgets/interviewSection/model/constants';
import ControlButton from '@/widgets/interviewSection/ui/ControlButton';

const switchStatus = (
  status: (typeof interviewStatus)[keyof typeof interviewStatus],
) => {
  // if (status === interviewStatus.BEFORE_INTERVIEW) {
  //   return preInterviewControlButtonProps;
  // }
  return interviewControlButtonProps;
};

function InterviewController() {
  const status = useInterviewRoomStore(state => state.status);

  return (
    <div
      aria-label="interview-controller"
      className="flex w-full items-center justify-center p-8"
    >
      <div
        aria-label="pre-interview-control-button-container"
        className="flex gap-4"
      >
        {switchStatus(status)?.map(({ onClick, label, text }) => (
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
