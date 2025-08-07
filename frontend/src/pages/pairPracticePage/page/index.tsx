import ChattingWindowContainer from '@/widgets/chattingWindow';
import CurrentQuestionDisplay from '@/widgets/interviewSection/ui/CurrentQuestionDisplay';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { useEffect } from 'react';
import { PRACTICE_MODE } from '@/pages/homePage/ui/PracticeButton';

function PairPracticePage() {
  const { connect, disconnect, roomCreate, interviewStart, interviewEnd } =
    usePracticeSessionStore();

  useEffect(() => {
    connect(PRACTICE_MODE.PAIR_PRACTICE, 1);
  }, []);
  return (
    <main aria-label="pair-practice-page">
      <CurrentQuestionDisplay />
      <ChattingWindowContainer />
    </main>
  );
}

export default PairPracticePage;
