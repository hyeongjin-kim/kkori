import { VideoOff } from 'lucide-react';

type Props = {
  visible: boolean;
  className?: string;
  label?: string;
};

export default function VideoPlaceholder({
  visible,
  className,
  label = '카메라 꺼짐',
}: Props) {
  if (!visible) return null;

  return (
    <div
      aria-label="video-placeholder"
      className={`pointer-events-none absolute inset-0 grid place-items-center ${className ?? ''}`}
    >
      <div className="flex flex-col items-center gap-2 rounded-md bg-white/90 p-4 shadow-lg backdrop-blur">
        <VideoOff className="h-7 w-7" />
        <span className="text-xs text-neutral-700">{label}</span>
      </div>
    </div>
  );
}
