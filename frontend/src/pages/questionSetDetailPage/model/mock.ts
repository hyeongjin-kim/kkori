import { QuestionSetResponse } from '@/entities/questionSet/model/response';

export const mockQuestionSet: QuestionSetResponse = {
  questionSetId: 1,
  title: '질문 세트 제목',
  description: '질문 세트 설명',
  versionNumber: 1,
  parentVersionId: null,
  isPublic: false,
  ownerNickname: '사용자명',
  questionMaps: [
    {
      mapId: 1,
      questionId: 10,
      answerId: 5,
      displayOrder: 1,
      question: {
        id: 10,
        content: '질문 내용',
        questionType: 1,
        createdBy: '사용자명',
        createdAt: '2025-01-01T00:00:00',
      },
      answer: {
        id: 5,
        content: '기대되는 답변',
        createdByNickname: '사용자명',
        createdAt: '2025-01-01T00:00:00',
      },
    },
  ],
  tailQuestions: [
    {
      id: 1,
      content: '꼬리 질문 내용',
      questionId: 10,
      createdBy: '사용자명',
      userAnswer: null,
      displayOrder: 1,
      createdAt: '2025-01-01T00:00:00',
      answeredAt: null,
    },
  ],
  tags: ['자바', '백엔드'],
  createdAt: '2025-01-01T00:00:00',
  updatedAt: '2025-01-01T00:00:00',
};
