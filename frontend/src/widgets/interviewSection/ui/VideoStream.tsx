import useBindMediaStream from '@/widgets/interviewSection/model/useBindMediaStream';
import { MediaStreamType } from '@/widgets/interviewSection/model/types';

function VideoStream({ type }: { type: MediaStreamType }) {
  const { videoRef, isVideoOn } = useBindMediaStream(type);

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
