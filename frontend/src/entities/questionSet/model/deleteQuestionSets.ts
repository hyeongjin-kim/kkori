import { del } from '@/shared/api/api';
import { DeleteQuestionSetResponse } from '@/entities/questionSet/model/response';

export const deleteQuestionSet = async (questionSetId: number) => {
  const response = await del<DeleteQuestionSetResponse>(
    `/api/questionsets/${questionSetId}`,
  );
  return response;
};
