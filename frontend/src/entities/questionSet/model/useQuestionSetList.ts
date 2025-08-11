import { useQuery } from '@tanstack/react-query';
import {
  getQuestionSet,
  getQuestionSets,
} from '@/entities/questionSet/model/getQuestionSets';
import { FIVE_MINUTES_IN_MS } from '@/app/model/constants';

export const useQuestionSetList = () => {
  return useQuery({
    queryKey: ['questionSets'],
    queryFn: getQuestionSets,
    staleTime: FIVE_MINUTES_IN_MS,
  });
};

export const useQuestionSet = (questionSetId: number) => {
  return useQuery({
    queryKey: ['questionSet', questionSetId],
    queryFn: () => getQuestionSet(questionSetId),
    staleTime: FIVE_MINUTES_IN_MS,
  });
};
