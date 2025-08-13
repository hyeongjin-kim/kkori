import { Mic, MicOff } from 'lucide-react';

type Props = {
  isAudioOn: boolean;
  onClick: () => void;
  className?: string;
};

export default function AudioStateDisplay({
  isAudioOn,
  onClick,
  className,
}: Props) {
  const label = isAudioOn ? '마이크 켜짐' : '마이크 꺼짐';
  return (
    <button
      type="button"
      onClick={onClick}
      aria-label={`audio-state-display`}
      title={label}
      className={`absolute right-4 bottom-4 z-20 inline-flex h-11 w-11 items-center justify-center rounded-full bg-white/90 shadow-lg backdrop-blur transition hover:bg-white focus:ring-2 focus:ring-blue-500/60 focus:outline-none active:scale-95 ${className ?? ''}`}
    >
      {isAudioOn ? <Mic className="h-5 w-5" /> : <MicOff className="h-5 w-5" />}
    </button>
  );
}
