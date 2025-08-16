import { useInterviewRecords } from '@/entities/interviewRecord/model/useInterviewRecords';
import InterviewRecordList from '@/pages/myInterviewResultPage/ui/InterviewRecordList';
import { useNavigate } from 'react-router-dom';

export default function MyInterviewResultContainer() {
  const { data, isLoading, isError } = useInterviewRecords({});
  const interviewRecords = data?.data.content ?? [];
  const navigate = useNavigate();
  const goDetail = (interviewId: number) =>
    navigate(`/interview-record-detail/${interviewId}`);
  if (isError) return <div className="p-4 text-red-500">불러오기 실패</div>;

  return (
    <InterviewRecordList
      interviewRecords={interviewRecords}
      isLoading={isLoading}
      onClick={goDetail}
    />
  );
}
