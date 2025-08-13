import useInterviewRoomStore, {
  interviewRole,
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import IntervieweeControlButtonContainer from '@/widgets/interviewSection/ui/IntervieweeControlButtonContainer';

const switchStatus = (
  role: (typeof interviewRole)[keyof typeof interviewRole],
  type: (typeof interviewType)[keyof typeof interviewType],
) => {
  if (type === interviewType.SOLO) {
    return <IntervieweeControlButtonContainer />;
  }
  if (role === interviewRole.INTERVIEWEE) {
    return <IntervieweeControlButtonContainer />;
  }
  return <IntervieweeControlButtonContainer />;
};

function InterviewController() {
  const status = useInterviewRoomStore(state => state.status);
  const role = useInterviewRoomStore(state => state.role);
  const type = useInterviewRoomStore(state => state.type);
  return (
    <div
      aria-label="interview-controller"
      className="flex w-full items-center justify-center p-8"
    >
      <div
        aria-label="pre-interview-control-button-container"
        className="flex gap-4"
      >
        {switchStatus(role, type)}
      </div>
    </div>
  );
}

export default InterviewController;
