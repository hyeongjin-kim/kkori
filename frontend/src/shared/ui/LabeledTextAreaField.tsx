import { useRef, useEffect } from 'react';

interface LabeledTextAreaFieldProps {
  displayTitle: string;
  label: string;
  placeholder: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
}

function LabeledTextAreaField({
  displayTitle,
  label,
  placeholder,
  value,
  onChange,
}: LabeledTextAreaFieldProps) {
  const ref = useRef<HTMLTextAreaElement | null>(null);
  useAutoResize(ref);

  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={label} className="text-sm font-medium text-gray-700">
        {displayTitle}
      </label>
      <textarea
        aria-label={label}
        ref={ref}
        className="focus:border-point-400 focus:ring-point-400 resize-none overflow-hidden rounded-lg border border-gray-200 bg-gray-50 px-4 py-2 text-sm text-gray-900 placeholder-gray-400 focus:ring-1 focus:outline-none"
        placeholder={placeholder}
        value={value}
        onChange={onChange}
      />
    </div>
  );
}

function useAutoResize(ref: React.RefObject<HTMLTextAreaElement | null>) {
  const autoResize = () => {
    if (!ref.current) return;
    ref.current.style.height = 'auto';
    ref.current.style.height = `${ref.current.scrollHeight}px`;
  };

  useEffect(() => {
    autoResize();
  }, [ref.current?.value]);
}

export default LabeledTextAreaField;
