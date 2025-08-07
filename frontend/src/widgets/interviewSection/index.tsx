import CurrentQuestionDisplay from '@/widgets/interviewSection/ui/CurrentQuestionDisplay';
import MediaStreamViewer from '@/widgets/interviewSection/ui/MediaStreamViewer';
import InterviewController from '@/widgets/interviewSection/ui/InterviewController';

function InterviewSection() {
  return (
    <div
      aria-label="interview-section"
      className="flex h-[calc(100vh-8rem)] w-2/3 flex-col gap-4"
    >
      <CurrentQuestionDisplay />
      <div
        aria-label="media-stream-viewer-container"
        className="flex h-full max-h-[24rem] w-full gap-4"
      >
        <MediaStreamViewer type="my" />
        <MediaStreamViewer type="peer" />
      </div>
      <InterviewController />
    </div>
  );
}

export default InterviewSection;
