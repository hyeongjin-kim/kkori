export default function InterviewRecordDetailSkeleton() {
  return (
    <div
      className="h-full w-full animate-pulse p-5"
      aria-label="interview-record-detail-skeleton"
    >
      <div className="mb-4 h-5 w-1/3 rounded bg-gray-200" />
      <div className="mb-3 h-4 w-1/2 rounded bg-gray-100" />
      <div className="mb-6 h-4 w-40 rounded bg-gray-100" />
      <div className="space-y-3">
        {Array.from({ length: 5 }).map((_, i) => (
          <div
            key={i}
            className="h-24 rounded-xl border border-gray-200 bg-white"
          />
        ))}
      </div>
    </div>
  );
}
