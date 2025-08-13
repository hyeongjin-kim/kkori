import { QuestionSetResponse } from '@/entities/questionSet/model/response';

export const questionSetList: QuestionSetResponse[] = [
  {
    questionSetId: 1,
    ownerId: 1,
    ownerNickname: '이찬',
    title: 'HTTP 기본',
    description: 'HTTP의 동작 원리와 특징에 대한 질문입니다.',
    versionNumber: 1,
    parentVersionId: null,
    isPublic: true,
    tags: [{ tag: 'HTTP' }, { tag: '네트워크' }],
    questionMaps: [
      {
        mapId: 1,
        questionId: 1,
        answerId: 1,
        displayOrder: 1,
        question: {
          id: 1,
          content: 'HTTP의 GET과 POST 방식 차이는?',
          questionType: 1,
          createdBy: '이찬',
          createdAt: '2025-01-01T00:00:00',
        },
        answer: {
          id: 1,
          content: 'GET은 데이터를 URL에 포함, POST는 본문에 포함.',
          createdByNickname: '이찬',
          createdAt: '2025-01-01T00:00:00',
        },
      },
    ],
    tailQuestions: [],
    createdAt: '2025-01-01T00:00:00',
    updatedAt: '2025-01-01T00:00:00',
  },
];
