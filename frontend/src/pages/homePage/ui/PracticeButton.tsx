export const PRACTICE_MODE = Object.freeze({
  SOLO_PRACTICE: 'SOLO_PRACTICE',
  PAIR_PRACTICE: 'PAIR_PRACTICE',
});

interface PracticeButtonProps {
  text: string;
  onClick: () => void;
  className?: string;
}

export function PracticeButton({
  text,
  onClick,
  className,
}: PracticeButtonProps) {
  return (
    <button
      onClick={onClick}
      className={`hover:bg-hover-gray bg-background flex h-[56px] max-w-[320px] min-w-[140px] items-center justify-center rounded-lg px-8 py-6 shadow-sm transition ${className} `}
    >
      <span className="text-lg font-semibold">{text}</span>
    </button>
  );
}
