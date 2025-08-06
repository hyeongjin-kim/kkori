import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';
import QuestionSet from '@/pages/interviewQuestionsPage/ui/QuestionSet';
import { questionSetList } from '@/entities/questionSet/model/mock';

function QuestionSetList() {
  const { questionSets, selectedTag } = useQuestionSetFilterStore();
  return (
    <ul
      aria-label="question-set-list"
      className="grid w-full grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2 lg:grid-cols-3"
    >
      {questionSetList
        .filter(questionSet =>
          selectedTag.id !== 0
            ? questionSet.tags.some(tag => tag.tag === selectedTag.tag)
            : true,
        )
        .map(questionSet => (
          <QuestionSet key={questionSet.id} questionSet={questionSet} />
        ))}
    </ul>
  );
}

export default QuestionSetList;
