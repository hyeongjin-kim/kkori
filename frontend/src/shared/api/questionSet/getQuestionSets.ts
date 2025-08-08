import { get } from '@/shared/api/api';
import { QuestionSet } from '@/entities/questionSet/model/response';

export const getQuestionSets = async () => {
  const response = await get<QuestionSet[]>('/api/question-sets');
  return response;
};

export const getQuestionSet = async (questionSetId: number) => {
  const response = await get<QuestionSet>(
    `/api/question-sets/${questionSetId}`,
  );
  return response;
};
