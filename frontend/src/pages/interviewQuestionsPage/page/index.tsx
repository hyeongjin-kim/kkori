import QuestionSetTagFilter from '@/pages/interviewQuestionsPage/ui/QuestionSetTagFilter';
import QuestionSetList from '@/pages/interviewQuestionsPage/ui/QuestionSetList';

function InterviewQuestionsPage() {
  return (
    <main
      aria-label="interview-questions-page"
      className="bg-background relative flex h-full max-h-screen w-full flex-col items-center justify-around gap-4 overflow-hidden px-30 py-8"
    >
      <QuestionSetTagFilter />
      <QuestionSetList />
    </main>
  );
}

export default InterviewQuestionsPage;
