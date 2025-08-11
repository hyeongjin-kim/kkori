// entities/questionSet/ui/QuestionSetQASection.tsx
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

  const totalQ = qMaps.length;
  const totalT = tqs.length;

  return (
    <SectionCard
      title="질문 · 답변 · 꼬리질문"
      ariaLabel="qa-with-tails"
      right={
        !isLoading && totalQ > 0 ? (
          <span className="text-sm text-gray-500">
            질문 {totalQ}개 · 꼬리 {totalT}개
          </span>
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
          {[...qMaps]
            .sort((a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0))
            .map(map => {
              const tails = (tailsByQuestionId[map.questionId] ?? []).sort(
                (a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0),
              );

              return (
                <li
                  key={map.mapId}
                  className="rounded-xl border border-gray-200 p-4 transition hover:shadow-sm"
                >
                  {/* 헤더(번호, ID) */}
                  <div className="mb-2 flex items-center gap-2">
                    <span className="inline-flex h-6 min-w-6 items-center justify-center rounded-full bg-gray-100 px-2 text-xs font-semibold text-gray-700">
                      {map.displayOrder}
                    </span>
                    <span className="text-xs text-gray-500">
                      QID {map.questionId} · AID {map.answerId}
                    </span>
                  </div>

                  {/* 질문 */}
                  <div className="mb-2 text-sm">
                    <div className="mb-1 text-[11px] font-medium tracking-wide text-gray-500 uppercase">
                      질문
                    </div>
                    <p className="rounded-lg bg-gray-50 p-3 whitespace-pre-wrap text-gray-900">
                      {map.question?.content}
                    </p>
                  </div>

                  {/* 답변 */}
                  <div className="text-sm">
                    <div className="mb-1 text-[11px] font-medium tracking-wide text-gray-500 uppercase">
                      기대되는 답변
                    </div>
                    <p className="rounded-lg bg-gray-50 p-3 whitespace-pre-wrap text-gray-900">
                      {map.answer?.content}
                    </p>
                  </div>

                  {/* 꼬리 질문(질문별로 1-뎁스) */}
                  <div className="mt-4">
                    <div className="mb-2 text-[11px] font-medium tracking-wide text-gray-500 uppercase">
                      꼬리 질문
                      {tails.length > 0 && (
                        <span className="ml-1 text-gray-400">
                          ({tails.length})
                        </span>
                      )}
                    </div>

                    {tails.length ? (
                      <ul className="flex flex-col gap-2">
                        {tails.map(tq => (
                          <li
                            key={tq.id}
                            className="rounded-lg border border-gray-100 bg-white p-3"
                          >
                            <div className="mb-1 flex items-center gap-2">
                              <Pill className="bg-gray-100 text-gray-700">
                                #{tq.displayOrder}
                              </Pill>
                              <span className="text-xs text-gray-400">
                                {tq.createdBy ? `작성자 ${tq.createdBy}` : ''}
                              </span>
                              <span className="text-xs text-gray-500">
                                {tq.answeredAt ? '답변 완료' : '미답변'}
                              </span>
                            </div>
                            <p className="text-sm text-gray-900">
                              {tq.content}
                            </p>
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <div className="rounded-lg bg-gray-50 px-3 py-2 text-xs text-gray-500">
                        해당 질문의 꼬리 질문이 없습니다.
                      </div>
                    )}
                  </div>
                </li>
              );
            })}
        </ol>
      ) : (
        <div className="rounded-lg border border-dashed border-gray-200 p-6 text-center text-sm text-gray-500">
          아직 등록된 질문이 없습니다.
        </div>
      )}
    </SectionCard>
  );
}
