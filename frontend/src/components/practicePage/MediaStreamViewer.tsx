import { MediaStreamType } from '@customTypes/practicePage/MediaStreamType';
import useBindMediaStream from '@hooks/practicePage/useBindMediaStream';
import VideoStream from '@/components/practicePage/VideoStream';
import VideoPlaceholder from '@/components/practicePage/VideoPlaceholder';
import AudioOffDisplay from '@/components/practicePage/AudioOffDisplay';

function MediaStreamViewer({ type }: MediaStreamType) {
  const { isVideoOn, isAudioOn } = useBindMediaStream({ type });

  return (
    <div
      aria-label="media-stream-viewer"
      className="relative h-full w-full overflow-hidden rounded-xl bg-black"
    >
      <VideoStream type={type} />
      <VideoPlaceholder visible={!isVideoOn} />
      <AudioOffDisplay visible={!isAudioOn} />
    </div>
  );
}

export default MediaStreamViewer;
