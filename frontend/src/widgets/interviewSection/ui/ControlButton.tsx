import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { useNavigate } from 'react-router-dom';

interface ControlButtonProps {
  onClick: () => void;
  label: string;
  text: string;
  status: (typeof interviewStatus)[keyof typeof interviewStatus];
  path?: string;
}

/**
 * 단일 버튼 스타일 가이드
 * - Glass 카드 위에서 잘 보이는 준그라데이션 + 섬세한 그림자
 * - 키보드 포커스 링 / 호버 리프트 / 액티브 프레스
 */
function ControlButton({
  onClick,
  label,
  text,
  status,
  path,
}: ControlButtonProps) {
  const currentStatus = useInterviewRoomStore(state => state.status);
  const shouldShow = status === 'always' ? true : currentStatus === status;
  const navigate = useNavigate();

  if (!shouldShow) return null;

  const handleClick = () => {
    onClick();
    if (path) navigate(path);
  };

  return (
    <button
      aria-label={`${label}-control-button`}
      onClick={handleClick}
      className="text-md inline-flex justify-start rounded-md border border-gray-200/80 bg-white px-6 py-4 font-semibold text-gray-700 shadow-md ring-1 ring-black/5 transition-all duration-150 hover:border-gray-300 hover:bg-gray-50 hover:text-black hover:opacity-90 hover:ring-black/10 focus:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500/60 focus-visible:ring-offset-2 active:scale-95"
    >
      <span className="relative z-10">{text}</span>
    </button>
  );
}

export default ControlButton;
