import { MediaStreamType } from '@customTypes/practicePage/MediaStreamType';
import useBindMediaStream from '@hooks/practicePage/useBindMediaStream';
import VideoStream from '@/components/practicePage/VideoStream';
import VideoPlaceholder from '@/components/practicePage/VideoPlaceholder';

function MediaStreamViewer({ type }: MediaStreamType) {
  const { videoRef, stream, isVideoOn, isAudioOn } = useBindMediaStream({
    type,
  });

  return (
    <div
      aria-label="media-stream-viewer"
      className="relative h-full w-full overflow-hidden rounded-xl bg-black"
    >
      <VideoStream type={type} />

      <VideoPlaceholder visible={!isVideoOn} />

      {!isAudioOn && (
        <div className="absolute right-2 bottom-2 rounded-full bg-red-600 p-1 text-white">
          <img
            src="/assets/mic-off.png"
            alt="mic-off-icon"
            className="h-4 w-4"
          />
        </div>
      )}
    </div>
  );
}

export default MediaStreamViewer;
