import QuestionSetTagFilter from '@/pages/interviewQuestionsPage/ui/QuestionSetTagFilter';
import Separator from '@/shared/ui/Separator';
import GoToButton from '@/shared/ui/GoToButton';
import { FcPlus } from 'react-icons/fc';
import QuestionSetListContainer from '../ui/QuestionSetListContainer';

function InterviewQuestionsPage() {
  return (
    <main
      aria-label="interview-questions-page"
      className="bg-background relative flex h-full max-h-screen w-full flex-col items-center justify-around gap-4 gap-8 overflow-hidden px-30 py-8"
    >
      <QuestionSetTagFilter />
      <GoToButton
        to="/question-set-create"
        label="question-set-create-button"
        text="질문 세트 생성하기"
        icon={<FcPlus className="mr-2 text-lg" />}
      />
      <Separator />
      <QuestionSetListContainer />
    </main>
  );
}

export default InterviewQuestionsPage;
