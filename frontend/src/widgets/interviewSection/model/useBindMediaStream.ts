import { useEffect, useRef } from 'react';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';
import { MediaStreamType } from '@/widgets/interviewSection/model/types';

interface BindResult {
  videoRef: React.RefObject<HTMLVideoElement | null>;
  stream: MediaStream | null;
  isVideoOn: boolean;
  isAudioOn: boolean;
}

function useBindMediaStream(type: MediaStreamType): BindResult {
  // TODO : multiple state picks 최적화, shallow 사용
  const myStream = useMediaStreamStore(state => state.myStream);
  const peerStream = useMediaStreamStore(state => state.peerStream);
  const isMyVideoOn = useMediaStreamStore(state => state.isMyVideoOn);
  const isMyAudioOn = useMediaStreamStore(state => state.isMyAudioOn);
  const isPeerVideoOn = useMediaStreamStore(state => state.isPeerVideoOn);
  const isPeerAudioOn = useMediaStreamStore(state => state.isPeerAudioOn);

  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    if (videoRef.current) {
      const stream = type === 'my' ? myStream : peerStream;
      videoRef.current.srcObject = stream;

      if (stream) {
        videoRef.current.load();
      }
    }
  }, [type, myStream, peerStream]);

  return {
    videoRef,
    stream: type === 'my' ? myStream : peerStream,
    isVideoOn: type === 'my' ? isMyVideoOn : isPeerVideoOn,
    isAudioOn: type === 'my' ? isMyAudioOn : isPeerAudioOn,
  };
}

export default useBindMediaStream;
