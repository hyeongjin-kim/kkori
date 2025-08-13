import { PracticeButton } from '@/pages/homePage/ui/PracticeButton';
import {
  interviewRole,
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { useEffect } from 'react';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';

function PairPracticeButton({
  role,
}: {
  role: (typeof interviewRole)[keyof typeof interviewRole];
}) {
  const roomId = '1234';
  useEffect(() => {
    usePracticeSessionStore.getState().setRoomId(roomId);
  }, [roomId]);

  const text =
    role === interviewRole.INTERVIEWER
      ? '같이 연습하기(면접관)'
      : '같이 연습하기(면접자)';

  return (
    <PracticeButton
      text={text}
      path="/pair-practice"
      className="border-point-300 text-point-300 border"
      mode={interviewType.PAIR}
      role={role}
    />
  );
}

export default PairPracticeButton;
