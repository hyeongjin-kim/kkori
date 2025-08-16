import { get } from '@/shared/api/api';
import { InterviewRecordRequest } from '@/entities/interviewRecord/model/request';
import {
  InterviewRecordDetailResponse,
  InterviewRecordListResponse,
} from '@/entities/interviewRecord/model/response';

export const getInterviewRecords = async (
  params: InterviewRecordRequest,
): Promise<{ data: { content: InterviewRecordListResponse[] } }> => {
  const response = await get<{
    data: { content: InterviewRecordListResponse[] };
  }>('/api/interview-records', {
    params,
  });
  return response;
};

export const getInterviewRecordDetail = async (
  interviewId: number,
): Promise<{ data: InterviewRecordDetailResponse }> => {
  const response = await get<{ data: InterviewRecordDetailResponse }>(
    `/api/interview-records/${interviewId}`,
  );
  return response;
};
