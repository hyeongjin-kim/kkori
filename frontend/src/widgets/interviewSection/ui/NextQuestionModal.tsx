import NextQuestionButton from '@/widgets/interviewSection/ui/NextQuestionButton';
import { useInterviewQuestionStore } from '@/widgets/interviewSection/model/useInterviewQuestionStore';
import {
  chooseCustomQuestion,
  chooseDefaultQuestion,
  chooseTailQuestion,
} from '@/widgets/interviewSection/model';
import Modal from '@/shared/ui/Modal';

function NextQuestionModal({
  onClose,
  contentRef,
}: {
  onClose: () => void;
  contentRef: React.RefObject<HTMLDivElement | null>;
}) {
  const { tailQuestion, defaultQuestion, customQuestion } =
    useInterviewQuestionStore();

  return (
    <Modal
      title="다음 질문을 선택하세요"
      subtitle="커스텀 질문을 선택하면 직접 질문을 만들 수 있습니다."
      onClose={onClose}
      contentRef={contentRef}
    >
      <div
        aria-label="next-question-modal"
        className="flex flex-col gap-6 p-4 px-5 py-2"
      >
        <NextQuestionButton
          nextQuestion={tailQuestion[0].question}
          label="tail-question-1"
          onClick={() => {
            chooseTailQuestion(0);
            onClose();
          }}
        />
        <NextQuestionButton
          nextQuestion={tailQuestion[1].question}
          label="tail-question-2"
          onClick={() => {
            chooseTailQuestion(1);
            onClose();
          }}
        />
        {defaultQuestion && (
          <NextQuestionButton
            nextQuestion={defaultQuestion.question}
            label="default-question"
            onClick={() => {
              chooseDefaultQuestion();
              onClose();
            }}
          />
        )}
        <NextQuestionButton
          nextQuestion={customQuestion}
          label="custom-question"
          onClick={() => {
            chooseCustomQuestion();
            onClose();
          }}
        />
      </div>
    </Modal>
  );
}

export default NextQuestionModal;
