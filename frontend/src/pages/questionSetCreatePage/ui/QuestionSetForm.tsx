import TitleInput from '@/entities/questionSet/ui/TitleInput';
import DescriptionInput from '@/entities/questionSet/ui/DescriptionInput';
import SharedToggleSwitch from '@/entities/questionSet/ui/SharedToggleSwitch';
import { useState } from 'react';
import TagListInput from '@/entities/questionSet/ui/TagListInput';
import TagDisplay from '@/entities/questionSet/ui/TagDisplay';

interface QuestionSetFormProps {
  title: string;
  description: string;
  isPublic: boolean;
  tagList: Set<string>;
  onChange: {
    title: (title: string) => void;
    description: (description: string) => void;
    isPublic: (isPublic: boolean) => void;
    tagList: (tagList: Set<string>) => void;
  };
}

function QuestionSetForm({
  title,
  description,
  isPublic,
  tagList,
  onChange,
}: QuestionSetFormProps) {
  const [tagInput, setTagInput] = useState('');

  const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange.title(e.target.value);
  };
  const handleDescriptionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChange.description(e.target.value);
  };

  const handleIsPublicChange = (value: boolean) => {
    onChange.isPublic(value);
  };

  const handleTagChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTagInput(e.target.value);
  };

  const handleTagSubmit = () => {
    onChange.tagList(new Set([...tagList, tagInput]));
    setTagInput('');
  };

  const handleTagClick = (tag: string) => {
    onChange.tagList(new Set([...tagList].filter(t => t !== tag)));
  };

  return (
    <section
      aria-label="question-set-form"
      className="w-2/3 min-w-[500px] rounded-xl border border-gray-200 bg-white p-8 shadow-sm"
    >
      <h3 className="mb-6 text-xl font-bold text-gray-900">질문 세트 설정</h3>
      <form className="flex flex-col gap-6">
        <TitleInput
          displayTitle="제목"
          value={title}
          onChange={handleTitleChange}
        />
        <DescriptionInput
          displayTitle="설명"
          value={description}
          onChange={handleDescriptionChange}
        />
        <SharedToggleSwitch
          displayTitle="공개 여부"
          value={isPublic}
          onChange={handleIsPublicChange}
        />
        <TagListInput
          displayTitle="태그"
          value={tagInput}
          onChange={handleTagChange}
          onSubmit={handleTagSubmit}
        />
        <TagDisplay tags={Array.from(tagList)} onClick={handleTagClick} />
      </form>
    </section>
  );
}

export default QuestionSetForm;
