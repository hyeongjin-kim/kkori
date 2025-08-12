import { useNavigate } from 'react-router-dom';
import { QuestionSetResponse } from '@/entities/questionSet/model/response';
import QuestionSetTagList from '@/pages/interviewQuestionsPage/ui/QusetionSetTagList';

interface QuestionSetProps {
  questionSet: QuestionSetResponse;
}

function QuestionSet({ questionSet }: QuestionSetProps) {
  const navigate = useNavigate();

  const goDetail = () =>
    navigate(`/question-set-detail/${questionSet.questionSetId}`);

  return (
    <li
      aria-label="question-set"
      role="button"
      tabIndex={0}
      onClick={goDetail}
      onKeyDown={e => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          goDetail();
        }
      }}
      className="relative flex w-full cursor-pointer flex-col gap-2 rounded-2xl border border-gray-200 bg-white p-5 shadow-sm ring-1 ring-gray-100 transition hover:border-gray-300 hover:shadow-md focus:outline-none focus-visible:ring-2 focus-visible:ring-[#3182F6] focus-visible:ring-offset-2"
    >
      <h3 className="mb-1 line-clamp-2 text-lg leading-snug font-bold text-gray-900">
        {questionSet.title}
      </h3>

      {questionSet.description && (
        <p className="mb-2 line-clamp-2 text-sm leading-relaxed font-semibold text-gray-500">
          {questionSet.description}
        </p>
      )}

      <div className="flex items-center justify-between gap-2">
        {questionSet.ownerNickname && (
          <p className="text-xs font-medium text-gray-500">
            작성자&nbsp;
            <span className="text-gray-700">{questionSet.ownerNickname}</span>
          </p>
        )}
        <QuestionSetTagList questionSet={questionSet} />
      </div>
    </li>
  );
}

export default QuestionSet;
