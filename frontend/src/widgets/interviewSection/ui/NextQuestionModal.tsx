import NextQuestionButton from '@/widgets/interviewSection/ui/NextQuestionButton';
import { useInterviewQuestionStore } from '@/widgets/interviewSection/model/useInterviewQuestionStore';
import {
  chooseCustomQuestion,
  chooseDefaultQuestion,
  chooseTailQuestion,
} from '../model';

function NextQuestionModal() {
  const { tailQuestion, defaultQuestion, customQuestion } =
    useInterviewQuestionStore();

  return (
    <div
      aria-label="next-question-modal"
      className="flex w-1/2 flex-col gap-4 rounded-2xl border border-gray-200 bg-white p-4 shadow-sm"
    >
      <span className="text-2xl font-bold">다음 질문을 선택하세요</span>
      <NextQuestionButton
        nextQuestion={tailQuestion.question}
        label="tail-question"
        onClick={chooseTailQuestion}
      />
      <NextQuestionButton
        nextQuestion={defaultQuestion.question}
        label="default-question"
        onClick={chooseDefaultQuestion}
      />
      <NextQuestionButton
        nextQuestion={customQuestion.question}
        label="custom-question"
        onClick={chooseCustomQuestion}
      />
    </div>
  );
}

export default NextQuestionModal;
