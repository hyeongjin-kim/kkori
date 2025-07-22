// PracticeButton.tsx
import { useNavigate } from 'react-router-dom';

interface PracticeButtonProps {
  text: string;
  path: string;
}

export default function PracticeButton({ text, path }: PracticeButtonProps) {
  const navigate = useNavigate();

  return (
    <button
      onClick={() => navigate(path)}
      className="flex h-[56px] min-w-[140px] items-center justify-center rounded-lg border border-[#2f3036] bg-[#1e1f22] px-4 py-3 shadow-sm transition hover:opacity-90"
    >
      <span className="text-sm font-medium text-[var(--color-text-white)]">
        {text}
      </span>
    </button>
  );
}
