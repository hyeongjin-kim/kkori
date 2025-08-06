import TagFilterList from '@/pages/interviewQuestionsPage/ui/TagFilterList';
import TagInputForm from '@/pages/interviewQuestionsPage/ui/TagInputForm';
import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';

function QuestionSetTagFilter() {
  const { selectedTag, setSelectedTag } = useQuestionSetFilterStore();
  return (
    <section
      aria-label="question-set-tag-filter"
      className="flex w-2/3 flex-col gap-4 rounded-md border border-gray-200 bg-white p-8 shadow-sm"
    >
      <p className="mb-2 text-xl font-bold">태그 필터</p>
      <TagFilterList selectedTag={selectedTag} onClick={setSelectedTag} />
      <TagInputForm />
    </section>
  );
}

export default QuestionSetTagFilter;
