import ChattingWindowContainer from '@/components/chattingWindow/ChattingWindowContainer';
import CurrentQuestionDisplay from '@/components/practicePage/CurrentQuestionDisplay';
import MediaStreamViewer from '@/components/practicePage/MediaStreamViewer';
import useInitMediaStream from '@/hooks/practicePage/useInitMediaStream';
import { useEffect } from 'react';

function PracticePage() {
  const { error } = useInitMediaStream();

  return (
    <main aria-label="practice-page">
      <CurrentQuestionDisplay id={1} question="현재 질문" />
      <MediaStreamViewer type="my" />
      <MediaStreamViewer type="peer" />
      <ChattingWindowContainer />
    </main>
  );
}

export default PracticePage;
