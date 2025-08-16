export default function QuestionSetSkeleton() {
  return (
    <li
      aria-label="question-set-skeleton"
      data-testid="question-set-skeleton"
      className="relative flex w-full animate-pulse flex-col gap-3 rounded-xl border border-gray-200 bg-white p-5 shadow-sm"
    >
      <div className="h-5 w-3/5 rounded bg-gray-200" />
      <div className="h-4 w-full rounded bg-gray-100" />
      <div className="h-4 w-5/6 rounded bg-gray-100" />
      <div className="h-4 w-32 rounded bg-gray-100" />
      <div className="mt-2 flex flex-wrap gap-2">
        <div className="h-6 w-16 rounded-full bg-gray-100" />
        <div className="h-6 w-14 rounded-full bg-gray-100" />
        <div className="h-6 w-20 rounded-full bg-gray-100" />
      </div>
    </li>
  );
}
