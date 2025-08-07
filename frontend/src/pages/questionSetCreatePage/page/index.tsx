import QuestionSetForm from '@/pages/questionSetCreatePage/ui/QuestionSetForm';
import QuestionAnswerForm from '@/pages/questionSetCreatePage/ui/QuestionAnswerForm';

function QuestionSetCreatePage() {
  return (
    <main
      aria-label="question-set-create-page"
      className="bg-background relative flex h-full w-full flex-col items-center justify-around gap-8 px-30 py-8"
    >
      <QuestionSetForm />
      <QuestionAnswerForm />
    </main>
  );
}

export default QuestionSetCreatePage;
