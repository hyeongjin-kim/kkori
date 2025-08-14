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
      className={`bg-point-500 text-text-black rounded-lg px-6 py-4 shadow-sm transition hover:opacity-80`}
    >
      <span className="relative z-10">{text}</span>
    </button>
  );
}

export default ControlButton;
