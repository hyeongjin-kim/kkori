import CurrentQuestionDisplay from '@/widgets/interviewSection/ui/CurrentQuestionDisplay';
import MediaStreamViewer from '@/widgets/interviewSection/ui/MediaStreamViewer';
import InterviewController from '@/widgets/interviewSection/ui/InterviewController';
import useMediaStreamStore from './model/useMediaStreamStore';

function InterviewSection() {
  const mainStreamType = useMediaStreamStore(state => state.mainStreamType);
  const subStreamType = useMediaStreamStore(state => state.subStreamType);

  return (
    <div
      aria-label="interview-section"
      className="flex h-[calc(100vh-8rem)] w-2/3 flex-col gap-4"
    >
      <CurrentQuestionDisplay />
      <div
        aria-label="media-stream-viewer-container"
        className="flex h-full max-h-[30rem] w-full gap-4"
      >
        <MediaStreamViewer type={mainStreamType} />
        <MediaStreamViewer type={subStreamType} />
      </div>
      <InterviewController />
    </div>
  );
}

export default InterviewSection;
