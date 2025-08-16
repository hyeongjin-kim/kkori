import {
  InterviewRecordDetailResponse,
  QuestionAnswerRecord,
} from '@/entities/interviewRecord/model/response';
import { formatDateTime } from '@/shared/lib/formatDateTime';
import Pill from '@/shared/ui/Pill';
import InterviewRecordDetailSkeleton from '@/pages/InterviewDetailPage/ui/InterviewReordDetailSkeleton';

function Row({
  label,
  value,
}: {
  label: string;
  value: string | number | undefined;
}) {
  return (
    <div className="flex flex-wrap items-center gap-2 text-sm">
      <span className="w-24 shrink-0 text-gray-500">{label}</span>
      <span className="font-medium text-gray-900">{value ?? '-'}</span>
    </div>
  );
}

function QAItem({ qa }: { qa: QuestionAnswerRecord }) {
  return (
    <li className="rounded-xl border border-gray-200 p-4">
      <div className="mb-2 flex items-center justify-between">
        <p className="text-sm font-semibold text-gray-900">
          Q{qa.orderNum}. {qa.questionContent}
        </p>
        <Pill color="gray">{qa.questionType}</Pill>
      </div>

      {qa.expectedAnswer && (
        <p className="mb-2 text-[13px] leading-relaxed text-gray-500">
          <span className="font-semibold text-gray-600">모범답안:</span>&nbsp;
          {qa.expectedAnswer}
        </p>
      )}

      {qa.parentQuestionId ? (
        <p className="mb-2 text-[13px] leading-relaxed text-gray-500">
          <span className="font-semibold text-gray-600">꼬리질문:</span>&nbsp;
          {qa.parentQuestionContent}
        </p>
      ) : null}

      <div className="mt-2 rounded-lg bg-gray-50 p-3">
        <p className="text-sm leading-relaxed whitespace-pre-wrap text-gray-900">
          {qa.answerContent || '—'}
        </p>
        <p className="mt-1 text-[11px] text-gray-500">
          답변 시간: {formatDateTime(qa.answeredAt)}
        </p>
      </div>
    </li>
  );
}

export default function InterviewRecordDetail({
  record,
  isLoading,
}: {
  record?: InterviewRecordDetailResponse;
  isLoading: boolean;
}) {
  if (isLoading) {
    return <InterviewRecordDetailSkeleton />;
  }

  if (!record) {
    return <div className="p-5 text-sm text-gray-500">데이터가 없습니다.</div>;
  }

  return (
    <div
      className="flex min-h-0 flex-1 flex-col"
      aria-label="interview-record-detail"
    >
      <div className="border-b border-gray-200 px-5 py-4">
        <h2 className="text-base font-semibold text-gray-900">
          {record.questionSetTitle}
        </h2>
        <p className="mt-1 text-[13px] text-gray-500">
          완료 시간 {formatDateTime(record.completedAt)}
        </p>
      </div>

      <div className="grid grid-cols-1 gap-3 border-b border-gray-200 px-5 py-4 md:grid-cols-2">
        <Row label="면접관" value={record.interviewerNickname} />
        <Row label="면접자" value={record.intervieweeNickname} />
        <Row label="Room ID" value={record.roomId} />
        <Row
          label="내 역할"
          value={record.userRole === 'INTERVIEWER' ? '면접관' : '면접자'}
        />
      </div>

      <div className="scrollbar-thin scrollbar-thumb-gray-300/70 scrollbar-track-transparent flex-1 overflow-y-auto p-5">
        <ol className="space-y-3">
          {record.questionAnswers?.map(qa => (
            <QAItem key={qa.recordId} qa={qa} />
          ))}
        </ol>
      </div>
    </div>
  );
}
