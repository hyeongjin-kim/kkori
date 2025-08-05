import { useEffect, useState } from 'react';
import useMediaStreamStore from '@/stores/useMediaStreamStore';

function useInitMediaStream() {
  const setMyStream = useMediaStreamStore(state => state.setMyStream);
  const setIsMyVideoOn = useMediaStreamStore(state => state.setIsMyVideoOn);
  const setIsMyAudioOn = useMediaStreamStore(state => state.setIsMyAudioOn);

  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const initMyStream = async () => {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: true,
          audio: true,
        });
        setMyStream(stream);
        setIsMyVideoOn(true);
        setIsMyAudioOn(true);
      } catch (error) {
        setError(error as string);
      }
    };
    initMyStream();
  }, []);

  return { error };
}

export default useInitMediaStream;
