export default function InterviewRecordListSkeleton() {
  return (
    <li
      aria-label="interview-record-list-skeleton"
      className="relative flex w-full animate-pulse flex-col gap-3 rounded-2xl border border-gray-200 bg-white p-5 shadow-sm"
    >
      <div className="h-5 w-3/5 rounded bg-gray-200" />
      <div className="h-4 w-4/5 rounded bg-gray-100" />
      <div className="grid grid-cols-3 gap-3">
        <div className="h-10 w-full rounded bg-gray-100" />
        <div className="h-10 w-full rounded bg-gray-100" />
        <div className="h-10 w-full rounded bg-gray-100" />
      </div>
    </li>
  );
}
