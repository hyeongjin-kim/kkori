import { useMyQuestionSets } from '@/entities/questionSet/model/useQuestionSetList';
import QuestionSetList from '@/pages/interviewQuestionsPage/ui/QuestionSetList';
import { useNavigate } from 'react-router-dom';

export default function MyQuestionSetListContainer() {
  const { data, isLoading, isError } = useMyQuestionSets({});
  const questionSets = data?.data.content ?? [];
  const navigate = useNavigate();
  const goDetail = (questionSetId: number) =>
    navigate(`/question-set-detail/${questionSetId}`);
  if (isError) return <div className="p-4 text-red-500">불러오기 실패</div>;

  return (
    <QuestionSetList
      questionSets={questionSets}
      isLoading={isLoading}
      onClick={goDetail}
    />
  );
}
