import QuestionSetTagFilter from '@/pages/interviewQuestionsPage/ui/QuestionSetTagFilter';

function InterviewQuestionsPage() {
  return (
    <main
      aria-label="interview-questions-page"
      className="bg-background relative flex h-full max-h-screen w-full items-center justify-around gap-4 overflow-hidden px-20 py-16"
    >
      <QuestionSetTagFilter />
    </main>
  );
}

export default InterviewQuestionsPage;
