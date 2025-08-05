import ChattingWindowContainer from '@/widgets/chattingWindow';
import CurrentQuestionDisplay from '@/widgets/interviewSection/ui/CurrentQuestionDisplay';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';
import { useWebSocketStore } from '@/stores/useWebSocketStore';
import { useEffect } from 'react';

function PairPracticePage() {
  return (
    <main aria-label="pair-practice-page">
      <CurrentQuestionDisplay />
      <ChattingWindowContainer />
    </main>
  );
}

export default PairPracticePage;
