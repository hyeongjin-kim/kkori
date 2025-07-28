import { CurrentQuestionDisplayProps } from '@/customTypes/practicePage/CurrentQuestionDisplayProps';

function CurrentQuestionDisplay({ id, question }: CurrentQuestionDisplayProps) {
  return (
    <div
      aria-label="current-question-display"
      className="text-center text-lg font-bold text-white"
      key={id}
    >
      {question}
    </div>
  );
}

export default CurrentQuestionDisplay;
