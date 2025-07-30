import useBindMediaStream from '@/hooks/practicePage/useBindMediaStream';
import { MediaStreamType } from '@customTypes/practicePage/MediaStreamType';

function VideoStream({ type }: MediaStreamType) {
  const { videoRef, isVideoOn } = useBindMediaStream({ type });

  return (
    <video
      ref={videoRef}
      autoPlay
      playsInline
      aria-label="video-stream"
      className={`h-full w-auto object-contain ${!isVideoOn ? 'opacity-0' : ''}`}
    />
  );
}

export default VideoStream;
