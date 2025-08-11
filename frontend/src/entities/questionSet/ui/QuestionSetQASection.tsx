import SectionCard from '@/shared/ui/SectionCard';
import SkeletonLine from '@/shared/ui/SkeletonLine';
import Pill from '@/shared/ui/Pill';

type QMap = {
  mapId: number;
  questionId: number;
  answerId: number;
  displayOrder?: number;
  question?: { content?: string };
  answer?: { content?: string };
};

type TailQ = {
  id: number;
  questionId: number;
  content: string;
  displayOrder?: number;
  answeredAt?: string | null;
  createdBy?: string;
};

export default function QuestionSetQASection({
  qs,
  isLoading,
}: {
  qs?: { questionMaps?: QMap[]; tailQuestions?: TailQ[] };
  isLoading?: boolean;
}) {
  const qMaps = qs?.questionMaps ?? [];
  const tqs = qs?.tailQuestions ?? [];

  const tailsByQuestionId = tqs.reduce<Record<number, TailQ[]>>((acc, cur) => {
    (acc[cur.questionId] ||= []).push(cur);
    return acc;
  }, {});

  const orderedQMaps = [...qMaps].sort(
    (a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0),
  );

  const totalQ = qMaps.length;
  const totalT = tqs.length;

  return (
    <SectionCard
      title="질문 · 답변 · 꼬리질문"
      ariaLabel="qa-with-tails"
      right={
        !isLoading && totalQ > 0 ? (
          <div className="flex items-center gap-2">
            <Pill className="bg-gray-100 text-gray-700">{totalQ}개의 질문</Pill>
            <Pill className="bg-gray-100 text-gray-700">
              {totalT}개의 꼬리 질문
            </Pill>
          </div>
        ) : null
      }
    >
      {isLoading ? (
        <div className="flex flex-col gap-3">
          <SkeletonLine />
          <SkeletonLine w="w-5/6" />
          <SkeletonLine w="w-4/6" />
        </div>
      ) : totalQ ? (
        <ol className="flex flex-col gap-4">
          {orderedQMaps.map((map, idx) => {
            const tails = (tailsByQuestionId[map.questionId] ?? []).sort(
              (a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0),
            );
            const orderBadge = map.displayOrder ?? idx + 1;

            return (
              <li
                key={map.mapId}
                className="rounded-2xl border border-gray-200 bg-white p-5 shadow-sm transition hover:shadow-md"
              >
                {/* 헤더(번호, 메타) */}
                <div className="mb-3 flex items-center gap-2">
                  <span className="inline-flex h-7 min-w-7 items-center justify-center rounded-lg bg-gray-100 px-2 text-xs font-semibold text-gray-800 ring-1 ring-gray-200">
                    {orderBadge}번째 질문 / 답변
                  </span>
                </div>

                {/* 질문 */}
                <div className="mb-3">
                  <div className="mb-1 text-[11px] font-medium tracking-wide text-gray-500">
                    질문
                  </div>
                  <div className="rounded-xl bg-gray-50 p-3 text-sm break-words whitespace-pre-wrap text-gray-900 ring-1 ring-gray-100">
                    {map.question?.content || '—'}
                  </div>
                </div>

                {/* 답변 */}
                <div>
                  <div className="mb-1 text-[11px] font-medium tracking-wide text-gray-500">
                    예상 답변
                  </div>
                  <div className="rounded-xl bg-gray-50 p-3 text-sm break-words whitespace-pre-wrap text-gray-900 ring-1 ring-gray-100">
                    {map.answer?.content || '—'}
                  </div>
                </div>

                {/* 섹션 구분선 */}
                <div className="my-4 h-px bg-gray-100" />

                {/* 꼬리 질문 */}
                <div>
                  <div className="mb-2 flex items-center gap-2 text-[11px] font-medium tracking-wide text-gray-500">
                    <span>꼬리 질문</span>
                    {tails.length > 0 && (
                      <Pill className="bg-gray-100 text-gray-600">
                        총 {tails.length}
                      </Pill>
                    )}
                  </div>

                  {tails.length ? (
                    <ul className="flex flex-col gap-2">
                      {tails.map(tq => {
                        const answered = Boolean(tq.answeredAt);
                        return (
                          <li
                            key={tq.id}
                            className="rounded-xl border border-gray-100 bg-white p-3 ring-1 ring-gray-50 transition hover:bg-gray-50"
                          >
                            <div className="mb-1 flex flex-wrap items-center gap-2">
                              <Pill className="bg-gray-100 text-gray-700">
                                #{tq.displayOrder ?? '-'}
                              </Pill>
                              {tq.createdBy && (
                                <span className="text-xs text-gray-400">
                                  작성자 {tq.createdBy}
                                </span>
                              )}
                              <Pill
                                className={
                                  answered
                                    ? 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-100'
                                    : 'bg-amber-50 text-amber-700 ring-1 ring-amber-100'
                                }
                              >
                                {answered ? '답변 완료' : '미답변'}
                              </Pill>
                            </div>
                            <p className="text-sm break-words whitespace-pre-wrap text-gray-900">
                              {tq.content}
                            </p>
                          </li>
                        );
                      })}
                    </ul>
                  ) : (
                    <div className="rounded-xl bg-gray-50 px-3 py-2 text-xs text-gray-500 ring-1 ring-gray-100">
                      해당 질문의 꼬리 질문이 없습니다.
                    </div>
                  )}
                </div>
              </li>
            );
          })}
        </ol>
      ) : (
        <div className="flex flex-col items-center justify-center gap-2 rounded-2xl border border-dashed border-gray-200 bg-white p-8 text-center">
          <div className="text-sm font-medium text-gray-900">
            아직 등록된 질문이 없습니다
          </div>
          <p className="text-xs text-gray-500">
            질문과 기대 답변을 추가해 시작해 보세요.
          </p>
        </div>
      )}
    </SectionCard>
  );
}
