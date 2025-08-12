import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  getQuestionSet,
  getQuestionSets,
} from '@/entities/questionSet/model/getQuestionSets';
import { FIVE_MINUTES_IN_MS, NO_STALE_TIME_MS } from '@/app/model/constants';
import {
  GetMyQuestionSetsParams,
  GetQuestionSetsParams,
  UpdateQuestionSetMetadataRequest,
  UpdateQuestionSetRequest,
} from '@/entities/questionSet/model/request';
import { postQuestionSet } from '@/entities/questionSet/model/postQuestionSet';
import { getMyQuestionSets } from '@/entities/questionSet/model/getQuestionSets';
import {
  putQuestionSetMetadata,
  putQuestionSets,
} from '@/entities/questionSet/model/putQuestionSets';
import { deleteQuestionSet } from './deleteQuestionSets';

export const useQuestionSets = (params: GetQuestionSetsParams) => {
  return useQuery({
    queryKey: ['questionSets', params],
    queryFn: () => getQuestionSets(params),
    staleTime: NO_STALE_TIME_MS,
  });
};

export const useMyQuestionSets = (params: GetMyQuestionSetsParams) => {
  return useQuery({
    queryKey: ['myQuestionSets', params],
    queryFn: () => getMyQuestionSets(params),
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

export const useCreateQuestionSet = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: postQuestionSet,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['questionSets'] });
    },
  });
};

export const useUpdateQuestionSet = (questionSetId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: UpdateQuestionSetRequest) =>
      putQuestionSets(questionSetId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['questionSet', questionSetId],
      });
      queryClient.invalidateQueries({ queryKey: ['questionSets'] });
      queryClient.invalidateQueries({ queryKey: ['myQuestionSets'] });
    },
  });
};

export const useUpdateQuestionSetMetadata = (questionSetId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: UpdateQuestionSetMetadataRequest) =>
      putQuestionSetMetadata(questionSetId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['questionSet', questionSetId],
      });
      queryClient.invalidateQueries({ queryKey: ['questionSets'] });
      queryClient.invalidateQueries({ queryKey: ['myQuestionSets'] });
    },
  });
};

export const useDeleteQuestionSet = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteQuestionSet,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['questionSets'] });
      queryClient.invalidateQueries({ queryKey: ['myQuestionSets'] });
      queryClient.invalidateQueries({ queryKey: ['questionSet'] });
    },
  });
};
