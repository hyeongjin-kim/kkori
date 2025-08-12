import { useInterviewQuestionStore } from '@/widgets/interviewSection/model/useInterviewQuestionStore';

function CurrentQuestionDisplay() {
  const { currentQuestion } = useInterviewQuestionStore(state => state);

  return (
    <div
      aria-label="current-question-display"
      className="text-md inline-block w-full rounded-full border border-blue-500 bg-blue-50 px-4 py-2 font-medium text-blue-700 shadow-sm"
    >
      ❓ {currentQuestion.question}
    </div>
  );
}

export default CurrentQuestionDisplay;
