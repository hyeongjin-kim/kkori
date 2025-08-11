// entities/questionSet/model/toOverviewVM.ts
import { QuestionSetResponse } from '../model/response';

export type QuestionSetOverviewVM = {
  title: string;
  description?: string;
  version: string;
  isPublic: boolean;
  owner?: string;
  tags: string[];
  createdAt: string;
  updatedAt: string;
};

const defaultVM: QuestionSetOverviewVM = {
  title: '',
  version: '',
  isPublic: false,
  tags: [],
  createdAt: '',
  updatedAt: '',
};

export function toOverviewVM(qs: QuestionSetResponse): QuestionSetOverviewVM {
  if (!qs) return defaultVM;
  const version =
    `v${qs.versionNumber}` +
    (qs.parentVersionId ? ` (forked from ${qs.parentVersionId})` : '');
  const tags = (qs.tags ?? []).map(t => t);
  return {
    title: qs.title,
    description: qs.description || '',
    version,
    isPublic: qs.isPublic,
    owner: qs.ownerNickname || '',
    tags,
    createdAt: formatDate(qs.createdAt),
    updatedAt: formatDate(qs.updatedAt),
  };
}

export function formatDate(iso?: string) {
  try {
    if (!iso) return '';
    return new Date(iso).toLocaleString();
  } catch {
    return iso ?? '';
  }
}
