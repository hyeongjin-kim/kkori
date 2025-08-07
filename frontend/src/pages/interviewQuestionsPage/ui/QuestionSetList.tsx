import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';
import QuestionSet from '@/pages/interviewQuestionsPage/ui/QuestionSet';
import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';
import { Tag } from '@/entities/questionSet/model/response';

function QuestionSetList() {
  const { questionSets, selectedTag } = useQuestionSetFilterStore();
  return (
    <ul
      aria-label="question-set-list"
      className="grid w-full grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2 lg:grid-cols-3"
    >
      {questionSets
        .filter(questionSet =>
          selectedTag !== TAG_FILTER_LIST[0].tag
            ? questionSet.tags.some((tag: Tag) => tag.tag === selectedTag)
            : true,
        )
        .map(questionSet => (
          <QuestionSet key={questionSet.id} questionSet={questionSet} />
        ))}
    </ul>
  );
}

export default QuestionSetList;
