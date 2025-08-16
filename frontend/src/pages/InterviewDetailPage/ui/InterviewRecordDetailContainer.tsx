import { useParams } from 'react-router-dom';
import { useInterviewRecordDetail } from '@/entities/interviewRecord/model/useInterviewRecords';
import InterviewRecordDetail from '@/pages/InterviewDetailPage/ui/InterviewRecordDetail';

export default function InterviewRecordDetailContainer() {
  const { interviewId = '' } = useParams();
  const idNum = Number(interviewId);
  const { data, isLoading, isError } = useInterviewRecordDetail(idNum);

  if (isError) return <div className="p-4 text-red-500">불러오기 실패</div>;

  return (
    <div aria-label="interview-record-detail-container">
      <InterviewRecordDetail record={data?.data} isLoading={isLoading} />
    </div>
  );
}
