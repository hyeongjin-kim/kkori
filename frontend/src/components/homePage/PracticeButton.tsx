// PracticeButton.tsx
import { useNavigate } from 'react-router-dom';
import { useWebSocketStore } from '@/stores/useWebSocketStore';
import { RoomCreateRequest } from '@/customTypes/practicePage/RoomCreateRequest';

interface PracticeButtonProps {
  text: string;
  path: string;
  className?: string;
}

export default function PracticeButton({
  text,
  path,
  className,
}: PracticeButtonProps) {
  const navigate = useNavigate();
  const connect = useWebSocketStore(state => state.connect);
  const roomCreate = useWebSocketStore(state => state.roomCreate);
  return (
    <button
      onClick={() => {
        connect();
        roomCreate({
          mode: 'SOLO_PRACTICE',
          questionSetId: 1,
        });
        navigate(path);
      }}
      className={`hover:bg-hover-gray bg-background flex h-[56px] max-w-[320px] min-w-[140px] items-center justify-center rounded-lg px-8 py-6 shadow-sm transition ${className} `}
    >
      <span className="text-lg font-semibold">{text}</span>
    </button>
  );
}
