interface SharedToggleSwitchProps {
  displayTitle: string;
  value: boolean;
  onChange: (value: boolean) => void;
}

function SharedToggleSwitch({
  displayTitle,
  value,
  onChange,
}: SharedToggleSwitchProps) {
  return (
    <div className="flex flex-col gap-2" aria-label="shared-toggle-switch">
      <label className="text-sm font-medium text-gray-700">
        {displayTitle}
      </label>
      <div
        role="switch"
        aria-checked={value}
        tabIndex={0}
        aria-label={`shared-toggle`}
        onKeyDown={e => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            onChange(!value);
          }
        }}
        onClick={() => onChange(!value)}
        className={`relative flex h-10 w-28 cursor-pointer items-center rounded-full p-1 transition-all duration-500 ${
          value ? 'bg-point-400' : 'bg-gray-300'
        } shadow-inner`}
      >
        <span
          className={`z-10 text-sm font-medium transition-all duration-300 ${
            value
              ? 'translate-x-0 pl-4 text-white'
              : 'translate-x-14 text-gray-200'
          }`}
        >
          {value ? '공개' : '비공개'}
        </span>
        <div
          className={`absolute top-1 left-1 h-8 w-8 rounded-full bg-white shadow-md transition-all duration-300 ${
            value ? 'translate-x-18' : 'translate-x-0'
          }`}
        />
      </div>
    </div>
  );
}

export default SharedToggleSwitch;
