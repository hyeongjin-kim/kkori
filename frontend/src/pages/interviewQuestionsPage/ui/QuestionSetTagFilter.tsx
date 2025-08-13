import TagInputForm from '@/pages/interviewQuestionsPage/ui/TagInputForm';
import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';
import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';
import TagFilter from '@/pages/interviewQuestionsPage/ui/TagFilter';
import { TagResponse } from '@/entities/questionSet/model/response';
import GoToButton from '@/shared/ui/GoToButton';

function QuestionSetTagFilter() {
  const { selectedTag, setSelectedTag } = useQuestionSetFilterStore();
  return (
    <section
      aria-label="question-set-tag-filter"
      className="flex w-full flex-col gap-4 rounded-md border border-gray-200 bg-white p-8 shadow-sm"
    >
      <div className="mb-4 flex justify-between">
        <p className="mb-2 text-xl font-bold">태그 필터</p>

        <GoToButton
          to="/question-set-create"
          label="question-set-create-button"
          text="질문 세트 생성하기"
          variant="ghost"
        />
      </div>
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
