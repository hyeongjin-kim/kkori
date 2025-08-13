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
    console.log(path);
    if (path) navigate(path);
    console.log('나가짐');
  };
  return (
    <button
      aria-label={`${label}-control-button`}
      onClick={handleClick}
      className="inline-flex justify-start rounded-md bg-white px-5 py-2 text-sm font-medium text-gray-700 shadow-sm transition-all duration-150 hover:border-gray-400 hover:bg-gray-50 hover:text-black active:scale-95"
      disabled={!shouldShow}
    >
      {text}
    </button>
  );
}

export default ControlButton;
