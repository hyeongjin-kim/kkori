import LabeledTextField from '@/shared/ui/LabeledTextField';

interface TagListInputProps {
  displayTitle: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: () => void;
}

function TagListInput({
  displayTitle,
  value,
  onChange,
  onSubmit,
}: TagListInputProps) {
  return (
    <LabeledTextField
      displayTitle={displayTitle}
      label="tag-input"
      placeholder="#태그를 입력하세요"
      value={value}
      onChange={onChange}
      onSubmit={onSubmit}
    />
  );
}

export default TagListInput;
