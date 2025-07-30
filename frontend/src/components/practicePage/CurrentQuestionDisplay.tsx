import { CurrentQuestionDisplayProps } from '@/customTypes/practicePage/CurrentQuestionDisplayProps';

function CurrentQuestionDisplay({ id, question }: CurrentQuestionDisplayProps) {
  return (
    <div
      aria-label="current-question-display"
      key={id}
      className="text-md inline-block rounded-full border border-blue-500 bg-blue-50 px-4 py-2 font-medium text-blue-700 shadow-sm"
    >
      ❓ {question}
    </div>
  );
}

export default CurrentQuestionDisplay;
