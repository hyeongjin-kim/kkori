import { useParams } from 'react-router-dom';
import { useQuestionSet } from '@/entities/questionSet/model/useQuestionSetList';

function QuestionSetDetailPage() {
  const { id } = useParams();
  const { data: questionSet } = useQuestionSet(Number(id));
  return (
    <main
      aria-label="question-set-detail-page"
      className="bg-background relative flex h-full w-full flex-col items-center justify-around gap-8 px-30 py-8"
    >
      <div>QuestionSetDetailPage</div>
    </main>
  );
}

export default QuestionSetDetailPage;
