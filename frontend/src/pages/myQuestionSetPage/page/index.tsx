import MyQuestionSetListContainer from '@/pages/myQuestionSetPage/ui/MyQuestionSetListContainer';
import GoToButton from '@/shared/ui/GoToButton';

function MyQuestionSetPage() {
  return (
    <main
      aria-label="my-question-set-page"
      className="relative h-full max-h-screen w-full bg-gray-50"
    >
      <div className="mx-auto flex h-full max-w-5xl flex-col px-4 pt-6 md:px-6 lg:px-8">
        <header className="mb-8 flex items-end justify-between px-4">
          <div className="space-y-1">
            <h1 className="mb-2 text-2xl font-semibold tracking-[-0.01em] text-gray-900">
              내 질문 세트
            </h1>
            <p className="text-sm text-gray-500">
              내가 만든 질문 세트 모음입니다. 버전과 공유 여부를 한눈에
              확인하세요.
            </p>
          </div>
          <GoToButton
            to="/question-set-create"
            label="question-set-create-button"
            text="새 세트 만들기"
          />
        </header>

        <section
          aria-label="my-question-set-list"
          className="relative flex min-h-0 flex-1 flex-col rounded-2xl border border-gray-200 bg-white shadow-sm"
        >
          <div className="flex items-center justify-between border-b border-gray-200 px-5 py-4">
            <h2 className="text-sm font-medium text-gray-900">전체 세트</h2>
          </div>

          <div className="scrollbar-thin scrollbar-thumb-gray-300/70 scrollbar-track-transparent h-full overflow-y-auto p-4 md:p-5">
            <MyQuestionSetListContainer />
          </div>
        </section>
        <div className="h-6" />
      </div>
    </main>
  );
}

export default MyQuestionSetPage;
