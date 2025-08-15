import useInterviewRoomStore, {
  interviewRole,
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import {
  pairIntervieweeControlButtonProps,
  soloIntervieweeControlButtonProps,
  pairInterviewerControlButtonProps,
} from '@/widgets/interviewSection/model/constants';
import ControlButton from '@/widgets/interviewSection/ui/ControlButton';

function InterviewController({ openModal }: { openModal: () => void }) {
  const role = useInterviewRoomStore(state => state.role);
  const type = useInterviewRoomStore(state => state.type);

  const controlButtonProps = (
    role: (typeof interviewRole)[keyof typeof interviewRole],
    type: (typeof interviewType)[keyof typeof interviewType],
  ) => {
    if (role === interviewRole.INTERVIEWEE) {
      if (type === interviewType.SOLO) {
        return soloIntervieweeControlButtonProps({
          openModal,
        });
      }
      return pairIntervieweeControlButtonProps({
        openModal,
      });
    }
    return pairInterviewerControlButtonProps({
      openModal,
    });
  };
  return (
    <div
      aria-label="interview-controller"
      className="flex w-full items-center justify-center gap-4 p-8"
    >
      <div
        aria-label="interviewee-control-button-container"
        className="flex gap-4"
      >
        {controlButtonProps(role, type).map(
          ({ label, onClick, text, status, path }) => (
            <ControlButton
              key={label}
              label={label}
              onClick={onClick}
              text={text}
              status={status}
              path={path}
            />
          ),
        )}
      </div>
    </div>
  );
}

export default InterviewController;
