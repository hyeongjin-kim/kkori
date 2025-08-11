import { useQuery } from '@tanstack/react-query';
import {
  getQuestionSet,
  getQuestionSets,
} from '@/entities/questionSet/model/getQuestionSets';
import { FIVE_MINUTES_IN_MS, NO_STALE_TIME_MS } from '@/app/model/constants';
import { GetQuestionSetsParams } from '@/entities/questionSet/model/request';

export const useQuestionSets = (params: GetQuestionSetsParams) => {
  return useQuery({
    queryKey: ['questionSets', params],
    queryFn: () => getQuestionSets(params),
    staleTime: NO_STALE_TIME_MS,
  });
};

export const useQuestionSet = (questionSetId: number) => {
  return useQuery({
    queryKey: ['questionSet', questionSetId],
    queryFn: () => getQuestionSet(questionSetId),
    staleTime: FIVE_MINUTES_IN_MS,
  });
};
