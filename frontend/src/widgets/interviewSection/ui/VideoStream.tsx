import { MediaStreamType } from '@/widgets/interviewSection/model/types';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';
import { useEffect, useRef } from 'react';

function VideoStream({ type }: { type: MediaStreamType }) {
  const videoRef = useRef<HTMLVideoElement>(null);
  let isVideoOn = false;
  let stream = null;
  let isAudioOn = false;

  if (type === 'peer') {
    stream = useMediaStreamStore(state => state.peerStream);
    isVideoOn = useMediaStreamStore(state => state.isPeerVideoOn);
    isAudioOn = useMediaStreamStore(state => state.isPeerAudioOn);
  } else {
    stream = useMediaStreamStore(state => state.myStream);
    isVideoOn = useMediaStreamStore(state => state.isMyVideoOn);
    isAudioOn = useMediaStreamStore(state => state.isMyAudioOn);
  }

  useEffect(() => {
    if (videoRef.current) {
      videoRef.current.srcObject = stream;
      videoRef.current.load();
    }
  }, [stream, videoRef]);

  return (
    <video
      ref={videoRef}
      autoPlay
      playsInline
      aria-label="video-stream"
      className={`h-full w-auto object-contain`}
    />
  );
}

export default VideoStream;
