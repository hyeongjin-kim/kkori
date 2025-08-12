import NextQuestionButton from '@/widgets/interviewSection/ui/NextQuestionButton';
import { useInterviewQuestionStore } from '@/widgets/interviewSection/model/useInterviewQuestionStore';
import {
  chooseCustomQuestion,
  chooseDefaultQuestion,
  chooseTailQuestion,
} from '../model';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';

function NextQuestionModal() {
  const { tailQuestion, defaultQuestion, customQuestion } =
    useInterviewQuestionStore();
  const { modalOpen } = useInterviewRoomStore();
  if (!modalOpen) return null;
  return (
    <div
      aria-label="next-question-modal"
      className="absolute top-1/2 left-1/2 z-10 flex h-1/2 w-1/2 -translate-x-1/2 -translate-y-1/2 flex-col gap-4 rounded-2xl border border-gray-200 bg-white p-4 px-5 py-2 shadow-sm"
    >
      <span className="text-2xl font-bold">다음 질문을 선택하세요</span>
      <NextQuestionButton
        nextQuestion={tailQuestion[0].question}
        label="tail-question-1"
        onClick={() => chooseTailQuestion(1)}
      />
      <NextQuestionButton
        nextQuestion={tailQuestion[1].question}
        label="tail-question-2"
        onClick={() => chooseTailQuestion(2)}
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
