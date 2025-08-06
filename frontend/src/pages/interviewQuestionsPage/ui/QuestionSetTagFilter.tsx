import TagFilterList from '@/pages/interviewQuestionsPage/ui/TagFilterList';
import TagInputForm from '@/pages/interviewQuestionsPage/ui/TagInputForm';
import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';

function QuestionSetTagFilter() {
  const { selectedTag, setSelectedTag } = useQuestionSetFilterStore();
  return (
    <section
      aria-label="question-set-tag-filter"
      className="flex flex-col gap-4"
    >
      <p className="text-lg font-bold">태그 필터</p>
      <TagFilterList selectedTag={selectedTag} onClick={setSelectedTag} />
      <TagInputForm />
    </section>
  );
}

export default QuestionSetTagFilter;
