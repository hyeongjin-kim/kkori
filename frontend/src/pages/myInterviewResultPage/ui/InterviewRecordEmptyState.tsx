import { Link } from 'react-router-dom';

export default function EmptyState() {
  return (
    <div
      aria-label="empty-interview-record"
      className="flex h-52 flex-col items-center justify-center gap-2 rounded-2xl border border-dashed border-gray-200 bg-white"
    >
      <p className="text-sm text-gray-500">아직 저장된 면접 기록이 없어요.</p>
      <Link
        to="/"
        className="rounded-full border border-gray-200 px-4 py-1.5 text-sm font-semibold text-gray-700 shadow-sm transition hover:bg-gray-50"
      >
        지금 인터뷰 시작하기
      </Link>
    </div>
  );
}
