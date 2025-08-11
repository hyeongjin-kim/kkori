import TagInputForm from '@/pages/interviewQuestionsPage/ui/TagInputForm';
import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';
import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';
import TagFilter from '@/pages/interviewQuestionsPage/ui/TagFilter';
import { TagResponse } from '@/entities/questionSet/model/response';

function QuestionSetTagFilter() {
  const { selectedTag, setSelectedTag } = useQuestionSetFilterStore();
  return (
    <section
      aria-label="question-set-tag-filter"
      className="flex w-2/3 flex-col gap-4 rounded-md border border-gray-200 bg-white p-8 shadow-sm"
    >
      <p className="mb-2 text-xl font-bold">태그 필터</p>
      <ul aria-label="tag-filter-list" className="flex flex-wrap gap-2">
        {TAG_FILTER_LIST.map(({ tag }: TagResponse) => (
          <TagFilter
            key={tag}
            tag={tag}
            selected={selectedTag === tag}
            onClick={() => setSelectedTag(tag)}
          />
        ))}
      </ul>
      <TagInputForm />
    </section>
  );
}

export default QuestionSetTagFilter;
