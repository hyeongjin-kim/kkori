interface LabeledTextFieldProps {
  displayTitle: string;
  label: string;
  placeholder: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

function LabeledTextField({
  displayTitle,
  label,
  placeholder,
  value,
  onChange,
}: LabeledTextFieldProps) {
  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={label} className="text-sm font-medium text-gray-500">
        {displayTitle}
      </label>
      <input
        type="text"
        aria-label={label}
        className="focus:border-point-400 focus:ring-point-400 rounded-lg border border-gray-200 bg-gray-50 px-4 py-2 text-sm text-gray-900 placeholder-gray-400 focus:ring-1 focus:outline-none"
        placeholder={placeholder}
        value={value}
        onChange={onChange}
      />
    </div>
  );
}

export default LabeledTextField;
