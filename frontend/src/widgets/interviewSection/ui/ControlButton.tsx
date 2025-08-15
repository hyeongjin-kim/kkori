import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { useNavigate } from 'react-router-dom';
import { controlStatus } from '../model/types';

interface ControlButtonProps {
  onClick: () => void;
  label: string;
  text: string;
  status: string;
  path?: string;
}

function shouldShow(status: string) {
  const currentStatus = useInterviewRoomStore(state => state.status);

  switch (status) {
    case controlStatus.ALWAYS:
      return true;
    case controlStatus.DURING_INTERVIEW:
      return (
        currentStatus !== interviewStatus.END_INTERVIEW &&
        currentStatus !== interviewStatus.BEFORE_INTERVIEW
      );
    default:
      return currentStatus === status;
  }
}

function ControlButton({
  onClick,
  label,
  text,
  status,
  path,
}: ControlButtonProps) {
  const navigate = useNavigate();

  if (!shouldShow(status)) return null;

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
