import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import IntervieweeControlButtonContainer from './IntervieweeControlButtonContainer';

const switchStatus = (
  role: 'interviewee' | 'interviewer',
  interviewType: 'solo' | 'pair',
) => {
  if (interviewType === 'solo') {
    return <IntervieweeControlButtonContainer />;
  }
  if (role === 'interviewee') {
    return <IntervieweeControlButtonContainer />;
  }
  return <IntervieweeControlButtonContainer />;
};

function InterviewController() {
  const status = useInterviewRoomStore(state => state.status);
  const role = useInterviewRoomStore(state => state.role);
  const interviewType = useInterviewRoomStore(state => state.type);
  return (
    <div
      aria-label="interview-controller"
      className="flex w-full items-center justify-center p-8"
    >
      <div
        aria-label="pre-interview-control-button-container"
        className="flex gap-4"
      >
        {switchStatus(role, interviewType)}
      </div>
    </div>
  );
}

export default InterviewController;
