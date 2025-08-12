import { useParams } from 'react-router-dom';
import { useQuestionSet } from '@/entities/questionSet/model/useQuestionSetList';
import QuestionSetQASection from '@/entities/questionSet/ui/QuestionSetQASection';
import QuestionSetOverviewSection from '@/entities/questionSet/ui/QuestionSetOverviewSection';

export default function QuestionSetDetailPage() {
  const { id } = useParams();
  const questionSetId = Number(id);
  const { data, isLoading, isError } = useQuestionSet(questionSetId);

  const qs = data?.data;
  console.log(qs);

  return (
    <main
      aria-label="question-set-detail-page"
      className="relative mx-auto flex min-h-screen w-full max-w-5xl flex-col gap-6 bg-gray-50 px-6 py-8"
    >
      <header className="flex flex-col gap-3">
        <QuestionSetOverviewSection />
      </header>
      <QuestionSetQASection qs={qs} isLoading={isLoading} />
    </main>
  );
}
