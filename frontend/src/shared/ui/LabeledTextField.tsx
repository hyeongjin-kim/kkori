interface LabeledTextFieldProps {
  displayTitle: string;
  label: string;
  placeholder: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit?: () => void;
}

function LabeledTextField({
  displayTitle,
  label,
  placeholder,
  value,
  onChange,
  onSubmit,
}: LabeledTextFieldProps) {
  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={label} className="text-sm font-medium text-gray-700">
        {displayTitle}
      </label>
      <input
        type="text"
        aria-label={label}
        className="focus:border-point-400 focus:ring-point-400 rounded-lg border border-gray-200 bg-gray-50 px-4 py-2 text-sm text-gray-900 placeholder-gray-400 focus:ring-1 focus:outline-none"
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        onKeyDown={e => {
          if (e.key === 'Enter') {
            e.preventDefault();
            onSubmit?.();
          }
        }}
      />
    </div>
  );
}

export default LabeledTextField;
