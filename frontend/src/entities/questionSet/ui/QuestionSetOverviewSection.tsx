// entities/questionSet/ui/QuestionSetOverviewSection.tsx
import { useParams, Link } from 'react-router-dom';
import { useQuestionSet } from '@/entities/questionSet/model/useQuestionSetList';
import { toOverviewVM } from '@/entities/questionSet/model/toOverviewVM';
import QuestionSetOverviewCard from './QuestionSetOverviewCard';
import Alert from '@/shared/ui/Alert';
import Skeleton from '@/pages/questionSetDetailPage/ui/Skeleton';
import { QueryBoundary } from '@/shared/ui/QueryBoundary';
import { QuestionSetResponse } from '@/entities/questionSet/model/response';

export default function QuestionSetOverviewSection() {
  const { id = '' } = useParams();

  return (
    <QueryBoundary
      pendingFallback={<Skeleton />}
      errorFallback={reset => (
        <Alert variant="error">
          데이터를 불러오는 중 문제가 발생했어요.{' '}
          <button className="underline" onClick={reset}>
            다시 시도
          </button>
        </Alert>
      )}
    >
      <Content id={id} />
    </QueryBoundary>
  );
}

function Content({ id }: { id: string }) {
  const { data } = useQuestionSet(Number(id));
  const vm = toOverviewVM(data?.data as QuestionSetResponse);
  return (
    <QuestionSetOverviewCard
      vm={vm}
      actions={
        <Link
          to="/interview-questions"
          className="rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm font-medium text-gray-700 shadow-sm transition hover:bg-gray-50 active:scale-[0.99]"
        >
          목록으로
        </Link>
      }
    />
  );
}
