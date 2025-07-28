import CurrentQuestionDisplay from '@/components/practicePage/CurrentQuestionDisplay';
import MediaStreamViewer from '@/components/practicePage/MediaStreamViewer';

function PracticePage() {
  return (
    <main aria-label="practice-page">
      <CurrentQuestionDisplay id={1} question="현재 질문" />
      <MediaStreamViewer type="my" />
      <MediaStreamViewer type="peer" />
    </main>
  );
}

export default PracticePage;
