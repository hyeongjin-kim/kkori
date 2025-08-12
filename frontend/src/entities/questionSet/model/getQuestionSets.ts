import { get } from '@/shared/api/api';
import {
  GetQuestionSetsResponse,
  QuestionSetResponse,
} from '@/entities/questionSet/model/response';
import {
  GetMyQuestionSetsParams,
  GetQuestionSetsParams,
} from '@/entities/questionSet/model/request';
import qs from 'qs';

export const getQuestionSet = async (questionSetId: number) => {
  const response = await get<{ data: QuestionSetResponse }>(
    `/api/questionsets/${questionSetId}`,
  );
  return response;
};

export const getQuestionSets = async (params: GetQuestionSetsParams) => {
  const response = await get<GetQuestionSetsResponse>(`/api/questionsets`, {
    params,
    paramsSerializer: {
      serialize: params =>
        qs.stringify(params, {
          arrayFormat: 'repeat',
          skipNulls: true,
        }),
    },
  });
  return response;
};

export const getMyQuestionSets = async (params: GetMyQuestionSetsParams) => {
  const response = await get<GetQuestionSetsResponse>(`/api/questionsets/my`, {
    params,
  });
  return response;
};
