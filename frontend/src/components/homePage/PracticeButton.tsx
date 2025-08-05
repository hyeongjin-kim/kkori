import { useNavigate } from 'react-router-dom';

interface PracticeButtonProps {
  text: string;
  path: string;
  className?: string;
  mode: 'SOLO_PRACTICE' | 'PAIR_PRACTICE';
}

export default function PracticeButton({
  text,
  path,
  className,
  mode,
}: PracticeButtonProps) {
  const navigate = useNavigate();
  return (
    <button
      onClick={() => {
        navigate(path);
      }}
      className={`hover:bg-hover-gray bg-background flex h-[56px] max-w-[320px] min-w-[140px] items-center justify-center rounded-lg px-8 py-6 shadow-sm transition ${className} `}
    >
      <span className="text-lg font-semibold">{text}</span>
    </button>
  );
}
