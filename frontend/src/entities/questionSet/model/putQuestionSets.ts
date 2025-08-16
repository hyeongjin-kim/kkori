import { put } from '@/shared/api/api';
import {
  UpdateQuestionSetMetadataRequest,
  UpdateQuestionSetRequest,
} from '@/entities/questionSet/model/request';
import { UpdateQuestionSetResponse } from '@/entities/questionSet/model/response';

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
