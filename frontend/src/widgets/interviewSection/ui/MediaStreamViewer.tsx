import { MediaStreamType } from '@/widgets/interviewSection/model/types';
import VideoStream from '@/widgets/interviewSection/ui/VideoStream';
import VideoPlaceholder from '@/widgets/interviewSection/ui/VideoPlaceholder';
import AudioStateDisplay from '@/widgets/interviewSection/ui/AudioStateDisplay';
import VideoStateDisplay from '@/widgets/interviewSection/ui/VideoStateDisplay';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';
import InterviewRoleBadge from '@/widgets/interviewSection/ui/InterviewRoleBadge';

function MediaStreamViewer({ type }: { type: MediaStreamType }) {
  let isAudioOn = false;
  let isVideoOn = false;
  let stream = null;

  if (type === 'my') {
    isAudioOn = useMediaStreamStore(state => state.isMyAudioOn);
    isVideoOn = useMediaStreamStore(state => state.isMyVideoOn);
    stream = useMediaStreamStore(state => state.myStream);
  } else {
    isAudioOn = useMediaStreamStore(state => state.isPeerAudioOn);
    isVideoOn = useMediaStreamStore(state => state.isPeerVideoOn);
    stream = useMediaStreamStore(state => state.peerStream);
  }

  const handleAudioStateChange = () => {
    if (type === 'my') {
      useMediaStreamStore.getState().setIsMyAudioOn(!isAudioOn);
    } else {
      useMediaStreamStore.getState().setIsPeerAudioOn(!isAudioOn);
    }
    stream?.getTracks().forEach(track => {
      if (track.kind === 'audio') track.enabled = !isAudioOn;
    });
  };

  const handleVideoStateChange = () => {
    if (type === 'my') {
      useMediaStreamStore.getState().setIsMyVideoOn(!isVideoOn);
    } else {
      useMediaStreamStore.getState().setIsPeerVideoOn(!isVideoOn);
    }
    stream?.getTracks().forEach(track => {
      if (track.kind === 'video') track.enabled = !isVideoOn;
    });
  };

  return (
    <div
      aria-label={`${type}-media-stream-viewer`}
      className="relative flex h-full w-full items-center justify-center overflow-hidden rounded-xl bg-black"
    >
      <InterviewRoleBadge type={type} />
      <VideoStream type={type} />
      <VideoPlaceholder visible={!isVideoOn} type={type} />
      {type === 'my' && (
        <div className="absolute right-4 bottom-4 z-10 flex gap-4">
          <AudioStateDisplay
            isAudioOn={isAudioOn}
            onClick={handleAudioStateChange}
          />
          <VideoStateDisplay
            isVideoOn={isVideoOn}
            onClick={handleVideoStateChange}
          />
        </div>
      )}
    </div>
  );
}

export default MediaStreamViewer;
