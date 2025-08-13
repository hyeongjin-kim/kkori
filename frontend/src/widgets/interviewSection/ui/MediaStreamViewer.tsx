import { MediaStreamType } from '@/widgets/interviewSection/model/types';
import useBindMediaStream from '@/widgets/interviewSection/model/useBindMediaStream';
import VideoStream from '@/widgets/interviewSection/ui/VideoStream';
import VideoPlaceholder from '@/widgets/interviewSection/ui/VideoPlaceholder';
import AudioStateDisplay from '@/widgets/interviewSection/ui/AudioStateDisplay';
import useMediaStreamStore from '../model/useMediaStreamStore';

//TODO : 타입 분리 고민하기
function MediaStreamViewer({ type }: { type: MediaStreamType }) {
  const { isVideoOn, isAudioOn } = useBindMediaStream(type);

  const handleAudioStateChange = () => {
    if (type === 'my') {
      isAudioOn
        ? useMediaStreamStore.getState().setIsMyAudioOn(false)
        : useMediaStreamStore.getState().setIsMyAudioOn(true);
    } else {
      isAudioOn
        ? useMediaStreamStore.getState().setIsPeerAudioOn(false)
        : useMediaStreamStore.getState().setIsPeerAudioOn(true);
    }
  };
  return (
    <div
      aria-label={`${type}-media-stream-viewer`}
      className="relative flex h-full w-full items-center justify-center overflow-hidden rounded-xl bg-black"
    >
      <VideoStream type={type} />
      <VideoPlaceholder visible={!isVideoOn} />
      <AudioStateDisplay
        isAudioOn={isAudioOn}
        onClick={handleAudioStateChange}
      />
    </div>
  );
}

export default MediaStreamViewer;
