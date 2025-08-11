import { MediaStreamType } from '@/widgets/interviewSection/model/types';
import useBindMediaStream from '@/widgets/interviewSection/model/useBindMediaStream';
import VideoStream from '@/widgets/interviewSection/ui/VideoStream';
import VideoPlaceholder from '@/widgets/interviewSection/ui/VideoPlaceholder';
import AudioOffDisplay from '@/widgets/interviewSection/ui/AudioOffDisplay';

//TODO : 타입 분리 고민하기
function MediaStreamViewer({ type }: { type: MediaStreamType }) {
  const { isVideoOn, isAudioOn } = useBindMediaStream(type);

  return (
    <div
      aria-label={`${type}-media-stream-viewer`}
      className="flex h-full w-full items-center justify-center overflow-hidden rounded-xl bg-black"
    >
      <VideoStream type={type} />
      <VideoPlaceholder visible={!isVideoOn} />
      {!isAudioOn && <AudioOffDisplay />}
    </div>
  );
}

export default MediaStreamViewer;
