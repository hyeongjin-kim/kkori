import { QuestionSetResponse } from '@/entities/questionSet/model/response';

interface QuestionSetProps {
  questionSet: QuestionSetResponse;
}

function QuestionSet({ questionSet }: QuestionSetProps) {
  return (
    <li
      aria-label="question-set"
      className="relative flex w-full flex-col gap-2 rounded-xl border border-gray-200 bg-white p-5 pb-10 shadow-sm transition hover:shadow-md"
    >
      <h3 className="text-base font-semibold text-gray-900">
        {questionSet.title}
      </h3>

      <p className="text-sm text-gray-600">{questionSet.description}</p>

      {questionSet.ownerNickname && (
        <p className="text-sm text-gray-500">
          작성자: {questionSet.ownerNickname}
        </p>
      )}

      <ul className="absolute right-4 bottom-4 flex flex-wrap gap-2 pt-1">
        {questionSet.tags.map((tag: string) => (
          <li
            key={tag}
            className="rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-700"
          >
            {tag}
          </li>
        ))}
      </ul>
    </li>
  );
}

export default QuestionSet;
