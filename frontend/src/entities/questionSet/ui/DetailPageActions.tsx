import { Link, useNavigate, useParams } from 'react-router-dom';
import { Copy, ChevronLeft, Pencil } from 'lucide-react';
import { postCopyQuestionSet } from '@/entities/questionSet/model/postQuestionSet';
import { QuestionSetOverviewVM } from '@/entities/questionSet/model/toOverviewVM';
import useUserStore from '@/entities/user/model/useUserStore';
import { useState } from 'react';
import { toast } from 'sonner';

function DetailPageActions({ vm }: { vm: QuestionSetOverviewVM }) {
  const { id: questionSetId } = useParams();
  const userId = useUserStore(state => state.userId);
  const isOwner = vm.ownerId === userId;
  const navigate = useNavigate();
  const [importing, setImporting] = useState(false);

  const handleImport = async () => {
    if (importing) return;
    try {
      setImporting(true);
      await postCopyQuestionSet({
        originalQuestionSetId: Number(questionSetId),
        title: vm.title,
        description: vm.description ?? '',
        copyTags: true,
      });
      toast.success('세트가 가져오기 되었습니다.');
      navigate('/my-question-set');
    } finally {
      setImporting(false);
    }
  };

  return (
    <div className="flex flex-wrap items-center gap-2 sm:gap-3">
      <Link
        to="/interview-questions"
        aria-label="back-to-list-button"
        className="inline-flex items-center gap-1.5 rounded-xl border border-gray-200 bg-white px-3.5 py-2 text-sm font-medium text-gray-700 shadow-sm transition hover:bg-gray-50 focus-visible:ring-2 focus-visible:ring-blue-500/50 focus-visible:outline-none active:scale-[0.99]"
      >
        <ChevronLeft className="h-4 w-4" />
        목록으로 돌아가기
      </Link>

      <span className="hidden h-5 w-px bg-gray-200 sm:block" />

      {isOwner ? (
        <button
          aria-label="edit-question-set-button"
          onClick={() => {}}
          className="inline-flex items-center gap-1.5 rounded-xl border border-blue-600 bg-white px-3.5 py-2 text-sm font-semibold text-blue-600 shadow-sm transition hover:bg-blue-50 focus-visible:ring-2 focus-visible:ring-blue-500/60 focus-visible:outline-none active:scale-[0.99]"
        >
          <Pencil className="h-4 w-4" />
          세트 수정하기
        </button>
      ) : (
        <button
          aria-label="import-question-set-button"
          onClick={handleImport}
          disabled={importing}
          className="inline-flex items-center gap-1.5 rounded-xl bg-blue-600 px-3.5 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 focus-visible:ring-2 focus-visible:ring-blue-500/60 focus-visible:outline-none active:scale-[0.99] disabled:cursor-not-allowed disabled:opacity-60"
        >
          <Copy className="h-4 w-4" />
          {importing ? '가져오는 중…' : '세트 가져오기'}
        </button>
      )}
    </div>
  );
}

export default DetailPageActions;
