interface ControlButtonProps {
  onClick: () => void;
  label: string;
  text: string;
}
function ControlButton({ onClick, label, text }: ControlButtonProps) {
  return (
    <button
      aria-label={`${label}-control-button`}
      onClick={onClick}
      className="inline-flex justify-start rounded-md bg-white px-5 py-2 text-sm font-medium text-gray-700 shadow-sm transition-all duration-150 hover:border-gray-400 hover:bg-gray-50 hover:text-black active:scale-95"
    >
      {text}
    </button>
  );
}

export default ControlButton;
