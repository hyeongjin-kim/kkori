import { useMyQuestionSets } from '@/entities/questionSet/model/useQuestionSetList';
import QuestionSetList from '@/pages/interviewQuestionsPage/ui/QuestionSetList';

export default function MyQuestionSetListContainer() {
  const { data, isLoading, isError } = useMyQuestionSets({});

  if (isError) return <div className="p-4 text-red-500">불러오기 실패</div>;

  return (
    <QuestionSetList
      questionSets={data?.data.content ?? []}
      isLoading={isLoading}
    />
  );
}
