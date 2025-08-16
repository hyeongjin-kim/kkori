export interface InterviewRecordListResponse {
  interviewId: number;
  roomId: string;
  interviewerNickname: string;
  intervieweeNickname: string;
  questionSetTitle: string;
  totalQuestionCount: number;
  completedAt: string;
  userRole: string;
}

export interface InterviewRecordDetailResponse {
  interviewId: number;
  roomId: string;
  interviewerNickname: string;
  intervieweeNickname: string;
  questionSetTitle: string;
  completedAt: string;
  userRole: string;
  questionAnswers: QuestionAnswerRecord[];
}

export interface QuestionAnswerRecord {
  recordId: number;
  orderNum: number;
  questionId: number;
  questionContent: string;
  questionType: string;
  expectedAnswer: string;
  parentQuestionId: number;
  parentQuestionContent: string;
  answerId: number;
  answerContent: string;
  answeredAt: string;
}
