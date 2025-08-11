import { get } from '@/shared/api/api';
import { QuestionSetResponse } from '@/entities/questionSet/model/response';

export const getQuestionSets = async () => {
  const response = await get<QuestionSetResponse[]>('/api/questionsets');
  return response;
};

export const getQuestionSet = async (questionSetId: number) => {
  const response = await get<{ data: QuestionSetResponse }>(
    `/api/questionsets/${questionSetId}`,
  );
  return response;
};
