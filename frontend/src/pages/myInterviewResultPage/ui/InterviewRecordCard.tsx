import { InterviewRecordListResponse } from '@/entities/interviewRecord/model/response';
import { formatDateTime } from '@/shared/lib/formatDateTime';
import Pill from '@/shared/ui/Pill';

function RoleBadge({ role }: { role: string }) {
  const isInterviewer = role?.toUpperCase() === 'INTERVIEWER';
  return (
    <span
      className={[
        'inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-[11px] font-semibold ring-1',
        isInterviewer
          ? 'bg-blue-50 text-blue-700 ring-blue-200'
          : 'bg-emerald-50 text-emerald-700 ring-emerald-200',
      ].join(' ')}
    >
      {isInterviewer ? '면접관' : '면접자'}
    </span>
  );
}

function Stat({ label, value }: { label: string; value: string | number }) {
  return (
    <div className="flex flex-col">
      <span className="text-[11px] font-medium text-gray-500">{label}</span>
      <span className="text-sm font-semibold text-gray-900">{value}</span>
    </div>
  );
}

export default function InterviewRecordCard({
  record,
  onClick,
}: {
  record: InterviewRecordListResponse;
  onClick: () => void;
}) {
  const {
    questionSetTitle,
    interviewerNickname,
    intervieweeNickname,
    totalQuestionCount,
    completedAt,
    roomId,
    userRole,
  } = record;

  const isSolo = interviewerNickname === intervieweeNickname;

  return (
    <li
      aria-label="interview-record"
      role="button"
      tabIndex={0}
      onClick={onClick}
      className="relative flex w-full cursor-pointer flex-col gap-3 rounded-2xl border border-gray-200 bg-white p-5 shadow-sm ring-1 ring-gray-100 transition hover:border-gray-300 hover:shadow-md focus:outline-none focus-visible:ring-2 focus-visible:ring-[#3182F6] focus-visible:ring-offset-2"
    >
      <div className="flex items-start justify-between gap-2">
        <h3 className="line-clamp-2 text-lg leading-snug font-bold text-gray-900">
          {questionSetTitle}
        </h3>
        <div className="flex items-center gap-2">
          <RoleBadge role={isSolo ? 'interviewee' : 'interviewer'} />
          <Pill color={isSolo ? 'violet' : 'amber'}>
            {isSolo ? '혼자 연습하기' : '같이 연습하기'}
          </Pill>
        </div>
      </div>

      <p className="text-sm font-medium text-gray-600">
        {isSolo ? (
          <>
            <span className="text-gray-500">면접자</span>&nbsp;
            <span className="text-gray-800">{intervieweeNickname}</span>
          </>
        ) : (
          <>
            <span className="text-gray-500">면접관</span>&nbsp;
            <span className="text-gray-800">{interviewerNickname}</span>
            <span className="mx-2 text-gray-300">·</span>
            <span className="text-gray-500">면접자</span>&nbsp;
            <span className="text-gray-800">{intervieweeNickname}</span>
          </>
        )}
      </p>

      <div className="mt-1 grid grid-cols-3 gap-3">
        <Stat label="질문 수" value={totalQuestionCount} />
        <Stat label="완료 시간" value={formatDateTime(completedAt)} />
        <Stat label="Room" value={roomId.slice(0, 6)} />
      </div>
    </li>
  );
}
