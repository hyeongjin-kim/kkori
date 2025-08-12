import QuestionSet from '@/pages/interviewQuestionsPage/ui/QuestionSet';
import { QuestionSetResponse } from '@/entities/questionSet/model/response';
import QuestionSetSkeleton from '@/pages/interviewQuestionsPage/ui/QuestionSetListSkeleton';

interface QuestionSetListProps {
  questionSets: QuestionSetResponse[];
  isLoading: boolean;
}

function QuestionSetList({ questionSets, isLoading }: QuestionSetListProps) {
  return (
    <ul
      aria-label="question-set-list"
      className="grid w-full grid-cols-1 gap-4 overflow-y-auto md:grid-cols-2 lg:grid-cols-3"
    >
      {isLoading ? (
        <QuestionSetSkeleton />
      ) : (
        questionSets.map(questionSet => (
          <QuestionSet
            key={questionSet.questionSetId}
            questionSet={questionSet}
          />
        ))
      )}
    </ul>
  );
}

export default QuestionSetList;
