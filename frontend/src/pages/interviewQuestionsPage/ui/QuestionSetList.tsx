import QuestionSet from '@/pages/interviewQuestionsPage/ui/QuestionSet';
import { QuestionSetResponse } from '@/entities/questionSet/model/response';
import QuestionSetSkeleton from '@/pages/interviewQuestionsPage/ui/QuestionSetListSkeleton';

interface QuestionSetListProps {
  questionSets: QuestionSetResponse[];
  isLoading: boolean;
  onClick: (questionSetId: number) => void;
}

function QuestionSetList({
  questionSets,
  isLoading,
  onClick,
}: QuestionSetListProps) {
  return (
    <ul
      aria-label="question-set-list"
      className="grid w-full grid-cols-1 gap-4 overflow-y-auto sm:grid-cols-2"
    >
      {isLoading ? (
        <QuestionSetSkeleton />
      ) : (
        questionSets.map(questionSet => (
          <QuestionSet
            key={questionSet.questionSetId}
            questionSet={questionSet}
            onClick={() => onClick(questionSet.questionSetId)}
          />
        ))
      )}
    </ul>
  );
}

export default QuestionSetList;
