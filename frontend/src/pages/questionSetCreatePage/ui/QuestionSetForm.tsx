import TitleInput from '@/entities/questionSet/ui/TitleInput';
import DescriptionInput from '@/entities/questionSet/ui/DescriptionInput';
import SharedToggleSwitch from '@/entities/questionSet/ui/SharedToggleSwitch';
import { useState } from 'react';
import TagListInput from '@/entities/questionSet/ui/TagListInput';
import TagDisplay from '@/entities/questionSet/ui/TagDisplay';

function QuestionSetForm() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [isShared, setIsShared] = useState(false);
  const [tagInput, setTagInput] = useState('');
  const [tagList, setTagList] = useState<Set<string>>(new Set());

  const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTitle(e.target.value);
  };

  const handleDescriptionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDescription(e.target.value);
  };

  const handleIsSharedChange = (value: boolean) => {
    setIsShared(value);
  };

  const handleTagChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTagInput(e.target.value);
  };

  const handleTagSubmit = () => {
    setTagList(prev => new Set([...prev, tagInput]));
    setTagInput('');
  };

  const handleTagClick = (tag: string) => {
    setTagList(prev => {
      const newSet = new Set(prev);
      newSet.delete(tag);
      return newSet;
    });
  };

  return (
    <section
      aria-label="question-set-form"
      className="w-2/3 rounded-xl border border-gray-200 bg-white p-8 shadow-sm"
    >
      <h3 className="mb-6 text-xl font-bold text-gray-900">질문 세트 생성</h3>
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
          value={isShared}
          onChange={handleIsSharedChange}
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
