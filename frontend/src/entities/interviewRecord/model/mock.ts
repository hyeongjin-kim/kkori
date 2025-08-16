import { InterviewRecordListResponse } from '@/entities/interviewRecord/model/response';

export const interviewRecordList: InterviewRecordListResponse[] = [
  {
    interviewId: 1,
    roomId: '1234567890',
    interviewerNickname: 'John Doe',
    intervieweeNickname: 'Jane Doe',
    questionSetTitle: 'Test Question Set',
    totalQuestionCount: 10,
    completedAt: '2021-01-01T00:00:00',
    userRole: 'INTERVIEWER',
  },
];
