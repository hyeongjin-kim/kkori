import { useState, useEffect, useMemo } from 'react';
import { useQuestionSets } from '@/entities/questionSet/model/useQuestionSetList';
import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';
import QuestionSetList from '@/pages/interviewQuestionsPage/ui/QuestionSetList';
import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';
import { useNavigate } from 'react-router-dom';

export default function QuestionSetListContainer() {
  const navigate = useNavigate();
  const selectedTag = useQuestionSetFilterStore(s => s.selectedTag);

  const [page, setPage] = useState(0);
  const size = 6;

  useEffect(() => setPage(0), [selectedTag]);

  const baseParams = useMemo(
    () =>
      selectedTag === TAG_FILTER_LIST[0].tag ? {} : { tags: [selectedTag] },
    [selectedTag],
  );

  const { data, isLoading, isError } = useQuestionSets({
    ...baseParams,
    page,
    size,
  } as any);

  const goDetail = (questionSetId: number) =>
    navigate(`/question-set-detail/${questionSetId}`);

  if (isError) return <div className="p-4 text-red-500">불러오기 실패</div>;

  const content = data?.data?.content ?? [];
  const totalPages = (data?.data?.totalPages as number) ?? undefined;
  const isLast =
    typeof data?.data?.last === 'boolean'
      ? (data?.data?.last as boolean)
      : content.length < size;

  return (
    <div className="flex h-full flex-col items-center gap-4">
      <div className="flex h-[480px] w-full max-w-[1200px] min-w-[980px]">
        <QuestionSetList
          questionSets={content}
          isLoading={isLoading}
          onClick={goDetail}
        />
      </div>

      <nav
        className="flex w-full max-w-[1200px] min-w-[980px] items-center justify-center gap-2"
        aria-label="pagination"
      >
        <button
          className="rounded border px-3 py-1 disabled:opacity-50"
          onClick={() => setPage(p => Math.max(0, p - 1))}
          disabled={page === 0 || isLoading}
        >
          이전
        </button>

        <span className="px-2 text-sm">
          {totalPages ? `${page + 1} / ${totalPages}` : `${page + 1}`}
        </span>

        <button
          className="rounded border px-3 py-1 disabled:opacity-50"
          onClick={() => setPage(p => p + 1)}
          disabled={isLast || isLoading}
        >
          다음
        </button>
      </nav>
    </div>
  );
}
