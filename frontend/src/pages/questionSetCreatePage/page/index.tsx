import QuestionSetForm from '@/pages/questionSetCreatePage/ui/QuestionSetForm';

function QuestionSetCreatePage() {
  return (
    <main
      aria-label="question-set-create-page"
      className="bg-background relative flex h-full max-h-screen w-full flex-col items-center justify-around gap-8 overflow-hidden px-30 py-8"
    >
      <QuestionSetForm />
    </main>
  );
}

export default QuestionSetCreatePage;
