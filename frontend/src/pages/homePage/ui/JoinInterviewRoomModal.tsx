import { useState, useRef, useEffect } from 'react';
import { Plus, KeyRound } from 'lucide-react';
import Modal from '@/shared/ui/Modal';
import { useNavigate } from 'react-router-dom';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { JOIN_ROOM_MODE } from '@/shared/lib/webSocketSlice';

interface JoinInterviewRoomModalProps {
  onClose: () => void;
  onCreate: () => void;
  contentRef: React.RefObject<HTMLDivElement | null>;
}

function JoinInterviewRoomModal({
  onClose,
  onCreate,
  contentRef,
}: JoinInterviewRoomModalProps) {
  const [roomCode, setRoomCode] = useState('');
  const setRoomId = usePracticeSessionStore(state => state.setRoomId);
  const setJoinRoomMode = usePracticeSessionStore(
    state => state.setJoinRoomMode,
  );
  const [error, setError] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const t = setTimeout(() => inputRef.current?.focus(), 50);
    return () => clearTimeout(t);
  }, []);

  const handleJoin = () => {
    if (!roomCode) {
      setError('방 코드를 입력해 주세요.');
      return;
    }
    setError(null);
    setRoomId(roomCode);
    setJoinRoomMode(JOIN_ROOM_MODE.JOIN_ROOM);
    navigate('/pair-practice');
  };

  const handleKeyDown: React.KeyboardEventHandler<HTMLInputElement> = e => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleJoin();
    }
  };

  return (
    <Modal
      title="면접 방 참여하기"
      subtitle="방을 생성하거나 다른 사용자의 방에 입장해보세요."
      onClose={onClose}
      contentRef={contentRef}
    >
      <div className="mx-auto w-full max-w-md">
        <label
          htmlFor="room-code"
          className="mb-2 block text-sm font-medium text-gray-900"
        >
          방 코드
        </label>
        <div className="flex items-center gap-2">
          <div className="relative flex-1">
            <input
              id="room-code"
              ref={inputRef}
              value={roomCode}
              onChange={e => setRoomCode(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="예: 6E69F7-080E03"
              className={[
                'w-full rounded-2xl border-0 ring-1 ring-inset',
                error ? 'ring-red-300' : 'ring-gray-200',
                'bg-white px-4 py-3 text-base',
                'placeholder:text-gray-400',
                'focus:ring-2 focus:ring-blue-500 focus:outline-none',
                'transition',
              ].join(' ')}
            />
            <KeyRound
              className="pointer-events-none absolute top-1/2 right-3 h-4 w-4 -translate-y-1/2 text-gray-400"
              aria-hidden
            />
          </div>

          <button
            type="button"
            onClick={handleJoin}
            className={[
              'shrink-0 rounded-2xl px-4 py-3 text-sm font-semibold',
              'bg-blue-600 text-white shadow-sm',
              'hover:bg-blue-700 active:scale-[0.99]',
              'focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500',
              'transition',
            ].join(' ')}
          >
            코드로 입장
          </button>
        </div>
        <p
          className={`mt-2 text-sm ${error ? 'text-red-600' : 'text-gray-500'}`}
        >
          {error ?? '초대받은 방의 코드를 붙여넣어 참여할 수 있어요.'}
        </p>

        <div className="my-6 flex items-center gap-3 text-xs text-gray-400">
          <div className="h-px flex-1 bg-gray-200" />
          <span>또는</span>
          <div className="h-px flex-1 bg-gray-200" />
        </div>

        <button
          type="button"
          onClick={onCreate}
          className={[
            'w-full rounded-2xl ring-1 ring-gray-200 ring-inset',
            'bg-gray-50 px-4 py-3 text-sm font-semibold text-gray-900',
            'hover:bg-gray-100 active:scale-[0.995]',
            'focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500',
            'flex items-center justify-center gap-2 transition',
          ].join(' ')}
        >
          <Plus className="h-4 w-4" />새 면접 방 만들기
        </button>
      </div>
    </Modal>
  );
}

export default JoinInterviewRoomModal;
