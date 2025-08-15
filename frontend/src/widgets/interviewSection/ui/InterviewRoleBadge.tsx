import useInterviewRoomStore, {
  interviewRole,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';

function InterviewRoleBadge({ type }: { type: 'my' | 'peer' }) {
  const role = useInterviewRoomStore(state => state.role);

  const oppositeRoleMap = {
    [interviewRole.INTERVIEWER]: interviewRole.INTERVIEWEE,
    [interviewRole.INTERVIEWEE]: interviewRole.INTERVIEWER,
  };

  const displayRole = type === 'my' ? role : oppositeRoleMap[role];

  const baseStyle =
    'inline-block absolute top-5 left-5 rounded-full px-3 py-1 text-sm font-semibold';
  const interviewerStyle = 'bg-blue-100 text-blue-800';
  const intervieweeStyle = 'bg-green-100 text-green-800';

  if (displayRole === interviewRole.INTERVIEWER) {
    return (
      <div
        aria-label="interviewer-role-badge"
        className={`${baseStyle} ${interviewerStyle}`}
      >
        면접관
      </div>
    );
  }

  return (
    <div
      aria-label="interviewee-role-badge"
      className={`${baseStyle} ${intervieweeStyle}`}
    >
      면접자
    </div>
  );
}

export default InterviewRoleBadge;
