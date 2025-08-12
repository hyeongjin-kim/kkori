import { Link } from 'react-router-dom';
import { postCopyQuestionSet } from '../model/postQuestionSet';

interface DetailPageActionsProps {
  id: string;
  title: string;
  description: string;
}

function DetailPageActions({ id, title, description }: DetailPageActionsProps) {
  const handleImport = () => {
    postCopyQuestionSet({
      originalQuestionSetId: Number(id),
      title: title,
      description: description ?? '',
      copyTags: true,
    });
  };

  return (
    <div className="flex gap-2">
      <Link
        to="/interview-questions"
        aria-label="back-to-list-button"
        className="rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm font-medium text-gray-700 shadow-sm transition hover:bg-gray-50 active:scale-[0.99]"
      >
        목록으로 돌아가기
      </Link>

      <button
        aria-label="import-question-set-button"
        onClick={handleImport}
        className="rounded-lg border border-blue-500 bg-blue-500 px-3 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-600 active:scale-[0.99]"
      >
        세트 가져오기
      </button>
    </div>
  );
}

export default DetailPageActions;
