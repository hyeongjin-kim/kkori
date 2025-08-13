import { useQuestionSets } from '@/entities/questionSet/model/useQuestionSetList';
import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';
import QuestionSetList from '@/pages/interviewQuestionsPage/ui/QuestionSetList';
import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';
import { useNavigate } from 'react-router-dom';

export default function QuestionSetListContainer() {
  const selectedTag = useQuestionSetFilterStore(s => s.selectedTag);
  const { data, isLoading, isError } = useQuestionSets({
    tags: selectedTag === TAG_FILTER_LIST[0].tag ? undefined : [selectedTag],
  });
  const navigate = useNavigate();
  const goDetail = (questionSetId: number) =>
    navigate(`/question-set-detail/${questionSetId}`);
  if (isError) return <div className="p-4 text-red-500">불러오기 실패</div>;

  return (
    <QuestionSetList
      questionSets={data?.data.content ?? []}
      isLoading={isLoading}
      onClick={goDetail}
    />
  );
}
