import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
interface ControlButtonProps {
  onClick: () => void;
  label: string;
  text: string;
  status: (typeof interviewStatus)[keyof typeof interviewStatus];
}
function ControlButton({ onClick, label, text, status }: ControlButtonProps) {
  const currentStatus = useInterviewRoomStore(state => state.status) !== status;

  return (
    <button
      aria-label={`${label}-control-button`}
      onClick={onClick}
      className="inline-flex justify-start rounded-md bg-white px-5 py-2 text-sm font-medium text-gray-700 shadow-sm transition-all duration-150 hover:border-gray-400 hover:bg-gray-50 hover:text-black active:scale-95"
      disabled={currentStatus}
    >
      {text}
    </button>
  );
}

export default ControlButton;
