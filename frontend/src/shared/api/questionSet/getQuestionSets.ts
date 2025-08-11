import { get } from '@/shared/api/api';
import { QuestionSet } from '@/entities/questionSet/model/response';

export const getQuestionSets = async () => {
  const response = await get<QuestionSet[]>('/api/questionsets');
  return response;
};

export const getQuestionSet = async (questionSetId: number) => {
  const response = await get<QuestionSet>(`/api/questionsets/${questionSetId}`);
  return response;
};
