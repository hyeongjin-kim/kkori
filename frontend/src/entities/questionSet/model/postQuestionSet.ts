import { post } from '@/shared/api/api';
import {
  CreateQuestionSetRequest,
  CopyQuestionSetRequest,
  UpdateQuestionSetRequest,
} from '@/entities/questionSet/model/request';
import {
  CreateQuestionSetResponse,
  UpdateQuestionSetResponse,
} from '@/entities/questionSet/model/response';

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

export const postVersionWithAnswerModifications = async (
  questionSetId: number,
  request: UpdateQuestionSetRequest,
) => {
  const response = await post<UpdateQuestionSetResponse>(
    `/api/questionsets/${questionSetId}/versions/with-answer-modifications`,
    request,
  );
  return response;
};
