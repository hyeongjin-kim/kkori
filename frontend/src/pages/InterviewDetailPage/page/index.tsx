import GoToButton from '@/shared/ui/GoToButton';
import InterviewRecordDetailContainer from '@/pages/InterviewDetailPage/ui/InterviewRecordDetailContainer';

export default function MyInterviewRecordDetailPage() {
  return (
    <main
      aria-label="my-interview-record-detail-page"
      className="relative h-full max-h-screen w-full bg-gray-50"
    >
      <div className="mx-auto flex h-full max-w-5xl flex-col px-4 pt-6 md:px-6 lg:px-8">
        <header className="mb-6 flex items-center justify-between px-1">
          <div className="space-y-1">
            <h1 className="text-2xl font-semibold tracking-[-0.01em] text-gray-900">
              면접 상세 기록
            </h1>
            <p className="text-sm text-gray-500">
              질문/답변과 진행 정보를 확인합니다.
            </p>
          </div>
          <GoToButton
            to="/my-interview-result"
            label="my-interview-result-button"
            text="목록으로"
          />
        </header>

        <section className="relative flex min-h-0 flex-1 flex-col rounded-2xl border border-gray-200 bg-white shadow-sm">
          <InterviewRecordDetailContainer />
        </section>
        <div className="h-6" />
      </div>
    </main>
  );
}
