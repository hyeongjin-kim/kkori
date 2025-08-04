import { useCurrentQuestionStore } from '@/stores/useCurrentQuestionStore';

function CurrentQuestionDisplay() {
  const { question } = useCurrentQuestionStore(state => state.question);

  return (
    <div className="w-full">
      <div
        aria-label="current-question-display"
        className="text-md inline-block rounded-full border border-blue-500 bg-blue-50 px-4 py-2 font-medium text-blue-700 shadow-sm"
      >
        ❓ {question}
      </div>
    </div>
  );
}

export default CurrentQuestionDisplay;
