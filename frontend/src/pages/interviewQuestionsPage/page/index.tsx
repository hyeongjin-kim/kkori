import QuestionSetTagFilter from '@/pages/interviewQuestionsPage/ui/QuestionSetTagFilter';
import Separator from '@/shared/ui/Separator';
import QuestionSetListContainer from '@/pages/interviewQuestionsPage/ui/QuestionSetListContainer';

function InterviewQuestionsPage() {
  return (
    <main
      aria-label="interview-questions-page"
      className="bg-background relative flex h-full max-h-screen w-full flex-col items-center gap-4 overflow-hidden px-30 py-8"
    >
      <QuestionSetTagFilter />
      <Separator />
      <QuestionSetListContainer />
    </main>
  );
}

export default InterviewQuestionsPage;
