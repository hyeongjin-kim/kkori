import { useRef } from 'react';
import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';
import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';

function TagInputForm() {
  const inputRef = useRef<HTMLInputElement>(null);
  const { setSelectedTag } = useQuestionSetFilterStore();

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const tag = inputRef.current?.value;
    setSelectedTag(tag || TAG_FILTER_LIST[0].tag);
  };

  return (
    <form
      aria-label="tag-input-form"
      className="flex w-full gap-2"
      onSubmit={handleSubmit}
    >
      <input
        ref={inputRef}
        type="text"
        placeholder="태그를 입력해주세요."
        aria-label="tag-input"
        className="border-point-400 text-point flex-1 rounded-md border p-2 focus:outline-none"
      />
      <button
        type="submit"
        className="bg-point-400 rounded-md px-4 py-2 text-white"
      >
        조회
      </button>
    </form>
  );
}

export default TagInputForm;
