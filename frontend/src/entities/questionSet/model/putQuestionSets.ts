import { put } from '@/shared/api/api';
import {
  UpdateQuestionSetMetadataRequest,
  UpdateQuestionSetRequest,
} from '@/entities/questionSet/model/request';
import { UpdateQuestionSetResponse } from '@/entities/questionSet/model/response';

export const putQuestionSets = async (
  questionSetId: number,
  request: UpdateQuestionSetRequest,
) => {
  const response = await put<UpdateQuestionSetResponse>(
    `/api/questionsets/${questionSetId}/revise`,
    request,
  );
  return response;
};

export const putQuestionSetMetadata = async (
  questionSetId: number,
  request: UpdateQuestionSetMetadataRequest,
) => {
  const response = await put<UpdateQuestionSetResponse>(
    `/api/questionsets/${questionSetId}/metadata`,
    request,
  );
  return response;
};
