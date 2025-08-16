import { InterviewRecordListResponse } from '@/entities/interviewRecord/model/response';
import InterviewRecordCard from '@/pages/myInterviewResultPage/ui/InterviewRecordCard';
import InterviewRecordListSkeleton from '@/pages/myInterviewResultPage/ui/InterviewRecordListSkeleton';
import EmptyState from '@/pages/myInterviewResultPage/ui/InterviewRecordEmptyState';

interface InterviewRecordListProps {
  interviewRecords: InterviewRecordListResponse[];
  isLoading: boolean;
  onClick: (interviewId: number) => void;
}

export default function InterviewRecordList({
  interviewRecords,
  isLoading,
  onClick,
}: InterviewRecordListProps) {
  if (isLoading) {
    return (
      <ul
        aria-label="interview-record-list-skeleton"
        className="grid w-full grid-cols-1 gap-4 sm:grid-cols-2"
      >
        {Array.from({ length: 6 }).map((_, i) => (
          <InterviewRecordListSkeleton key={i} />
        ))}
      </ul>
    );
  }

  if (!interviewRecords.length) {
    return <EmptyState />;
  }

  return (
    <ul
      aria-label="interview-record-list"
      className="grid w-full grid-cols-1 gap-4 sm:grid-cols-2"
    >
      {interviewRecords.map(item => (
        <InterviewRecordCard
          key={item.interviewId}
          record={item}
          onClick={() => onClick(item.interviewId)}
        />
      ))}
    </ul>
  );
}
