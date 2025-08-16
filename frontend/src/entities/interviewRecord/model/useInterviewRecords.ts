import { useQuery } from '@tanstack/react-query';
import {
  getInterviewRecordDetail,
  getInterviewRecords,
} from '@/entities/interviewRecord/model/getInterviewRecords';
import { InterviewRecordRequest } from '@/entities/interviewRecord/model/request';
import { FIVE_MINUTES_IN_MS } from '@/app/model/constants';

export const useInterviewRecords = (params: InterviewRecordRequest) => {
  return useQuery({
    queryKey: ['interviewRecords', params],
    queryFn: () => getInterviewRecords(params),
    staleTime: FIVE_MINUTES_IN_MS,
  });
};

export const useInterviewRecordDetail = (interviewId: number) => {
  return useQuery({
    queryKey: ['interviewRecordDetail', interviewId],
    queryFn: () => getInterviewRecordDetail(interviewId),
    staleTime: FIVE_MINUTES_IN_MS,
  });
};
