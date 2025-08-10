import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';
import {
  soloInterviewControlButtonProps,
  peerIntervieweeControlButtonProps,
  peerInterviewerControlButtonProps,
} from '@/widgets/interviewSection/model/constants';
import ControlButton from '@/widgets/interviewSection/ui/ControlButton';

const switchStatus = (
  status: (typeof interviewStatus)[keyof typeof interviewStatus],
  role: 'interviewee' | 'interviewer',
  interviewType: 'solo' | 'pair',
) => {
  const phase =
    status === interviewStatus.BEFORE_INTERVIEW ? 'preInterview' : 'interview';

  if (interviewType === 'solo') {
    return soloInterviewControlButtonProps[phase];
  }

  const map = {
    interviewee: peerIntervieweeControlButtonProps,
    interviewer: peerInterviewerControlButtonProps,
  };

  return map[role][phase];
};

function InterviewController() {
  const status = useInterviewRoomStore(state => state.status);
  const role = useInterviewRoomStore(state => state.role);
  const interviewType = useInterviewRoomStore(state => state.interviewType);
  return (
    <div
      aria-label="interview-controller"
      className="flex w-full items-center justify-center p-8"
    >
      <div
        aria-label="pre-interview-control-button-container"
        className="flex gap-4"
      >
        {switchStatus(status, role, interviewType)?.map(
          ({ onClick, label, text }) => (
            <ControlButton
              key={label}
              onClick={onClick}
              label={label}
              text={text}
            />
          ),
        )}
      </div>
    </div>
  );
}

export default InterviewController;
