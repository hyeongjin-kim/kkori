// entities/questionSet/ui/QuestionSetOverviewCard.tsx
import Pill from '@/shared/ui/Pill';
import SectionCard from '@/shared/ui/SectionCard';
import Separator from '@/shared/ui/Separator';
import type { QuestionSetOverviewVM } from '../model/toOverviewVM';

export default function QuestionSetOverviewCard({
  vm,
  actions,
}: {
  vm: QuestionSetOverviewVM;
  actions?: React.ReactNode;
}) {
  return (
    <SectionCard
      title="질문 세트"
      ariaLabel="question-set-overview"
      right={actions}
    >
      <h1 className="mb-2 text-xl leading-snug font-bold text-gray-900">
        {vm.title}
      </h1>

      <div className="my-3 flex flex-wrap items-center gap-2">
        <Pill color="gray">{vm.version}</Pill>
        <Pill color={vm.isPublic ? 'emerald' : 'gray'}>
          {vm.isPublic ? '공개' : '비공개'}
        </Pill>
        {vm.owner && <Pill color="blue">작성자 : {vm.owner}</Pill>}
        {vm.tags.length > 0 && (
          <div className="ml-1 flex flex-wrap gap-2">
            {vm.tags.map(tag => (
              <Pill key={tag.tag} color="gray">
                #{tag.tag}
              </Pill>
            ))}
          </div>
        )}
      </div>

      {vm.description && (
        <p className="mb-4 text-sm leading-6 text-gray-600">{vm.description}</p>
      )}

      <Separator />

      <dl className="grid grid-cols-1 gap-4 text-sm text-gray-600 sm:grid-cols-2">
        <div className="flex items-center justify-between rounded-xl bg-gray-50 px-4 py-3">
          <dt className="text-gray-500">생성일</dt>
          <dd className="font-medium text-gray-800">{vm.createdAt}</dd>
        </div>
        <div className="flex items-center justify-between rounded-xl bg-gray-50 px-4 py-3">
          <dt className="text-gray-500">수정일</dt>
          <dd className="font-medium text-gray-800">{vm.updatedAt}</dd>
        </div>
      </dl>
    </SectionCard>
  );
}
