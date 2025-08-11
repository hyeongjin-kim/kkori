import LabeledTextField from '@/shared/ui/LabeledTextField';

interface DescriptionInputProps {
  displayTitle: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

function DescriptionInput({
  displayTitle,
  value,
  onChange,
}: DescriptionInputProps) {
  return (
    <LabeledTextField
      displayTitle={displayTitle}
      label="description-input"
      placeholder="간단한 설명을 입력하세요"
      value={value}
      onChange={onChange}
    />
  );
}

export default DescriptionInput;
