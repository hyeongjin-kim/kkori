import { post } from '@/shared/api/api';
import {
  CreateQuestionSetRequest,
  CopyQuestionSetRequest,
} from '@/entities/questionSet/model/request';
import { CreateQuestionSetResponse } from '@/entities/questionSet/model/response';

export const postQuestionSet = async (
  questionSet: CreateQuestionSetRequest,
) => {
  const response = await post<CreateQuestionSetResponse>(
    '/api/questionsets',
    questionSet,
  );
  return response;
};

export const postCopyQuestionSet = async (
  questionSet: CopyQuestionSetRequest,
) => {
  const response = await post<CreateQuestionSetResponse>(
    '/api/questionsets/copy',
    questionSet,
  );
  return response;
};
