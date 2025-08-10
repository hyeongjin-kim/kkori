import { NextQuestionButtonProps } from '@/widgets/interviewSection/model/types';

function NextQuestionButton({
  nextQuestion,
  label,
  onClick,
}: NextQuestionButtonProps) {
  return (
    <button
      aria-label={label}
      onClick={onClick}
      className="text-md inline-flex items-center rounded-2xl border border-transparent bg-blue-100 px-4 py-2 font-bold text-gray-700 shadow-sm transition-[background-color,border-color,transform] duration-150 hover:border-blue-400 hover:bg-blue-300 hover:text-black disabled:opacity-50"
    >
      {nextQuestion}
    </button>
  );
}

export default NextQuestionButton;
