import LabeledTextField from '@/shared/ui/LabeledTextField';

interface TitleInputProps {
  displayTitle: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

function TitleInput({ displayTitle, value, onChange }: TitleInputProps) {
  return (
    <LabeledTextField
      displayTitle={displayTitle}
      label="title-input"
      placeholder="질문 세트의 제목을 입력하세요"
      value={value}
      onChange={onChange}
    />
  );
}

export default TitleInput;
