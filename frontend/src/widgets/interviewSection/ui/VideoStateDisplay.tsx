import { Video, VideoOff } from 'lucide-react';

type Props = {
  isVideoOn: boolean;
  onClick: () => void;
  className?: string;
};

export default function VideoStateDisplay({
  isVideoOn,
  onClick,
  className,
}: Props) {
  const label = isVideoOn ? '카메라 켜짐' : '카메라 꺼짐';
  return (
    <button
      type="button"
      onClick={onClick}
      aria-label={`video-state-display`}
      title={label}
      className={`flex h-11 w-11 items-center justify-center rounded-full bg-white/90 shadow-lg backdrop-blur transition hover:bg-white focus:ring-2 focus:ring-blue-500/60 focus:outline-none active:scale-95 ${className ?? ''}`}
    >
      {isVideoOn ? (
        <Video className="h-5 w-5" />
      ) : (
        <VideoOff className="h-5 w-5" />
      )}
    </button>
  );
}
