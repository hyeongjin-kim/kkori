import TitleInput from '@/entities/questionSet/ui/TitleInput';
import DescriptionInput from '@/entities/questionSet/ui/DescriptionInput';
import SharedToggleSwitch from '@/entities/questionSet/ui/SharedToggleSwitch';
import { useState } from 'react';

function QuestionSetForm() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [isShared, setIsShared] = useState(false);
  const [tagList, setTagList] = useState<string[]>([]);

  const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTitle(e.target.value);
  };

  const handleDescriptionChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDescription(e.target.value);
  };

  const handleIsSharedChange = (value: boolean) => {
    setIsShared(value);
  };

  return (
    <section
      aria-label="question-set-form"
      className="w-2/3 rounded-xl border border-gray-200 bg-white p-8 shadow-sm"
    >
      <h3 className="mb-6 text-xl font-bold text-gray-900">질문 세트 생성</h3>
      <form className="flex flex-col gap-6">
        <TitleInput
          displayTitle="면접 질문 세트 제목"
          value={title}
          onChange={handleTitleChange}
        />
        <DescriptionInput
          displayTitle="면접 질문 세트 설명"
          value={description}
          onChange={handleDescriptionChange}
        />
        <SharedToggleSwitch
          displayTitle="면접 질문 세트 공개 여부"
          value={isShared}
          onChange={handleIsSharedChange}
        />

        <div className="flex flex-col gap-1">
          <label htmlFor="tag" className="text-sm font-medium text-gray-500">
            태그
          </label>
          <input
            type="text"
            id="tag"
            aria-label="tag-input"
            className="focus:border-point-400 focus:ring-point-400 rounded-lg border border-gray-200 bg-gray-50 px-4 py-2 text-sm text-gray-900 placeholder-gray-400 focus:ring-1 focus:outline-none"
            placeholder="#태그를 입력하세요"
          />
        </div>
      </form>
    </section>
  );
}

export default QuestionSetForm;
