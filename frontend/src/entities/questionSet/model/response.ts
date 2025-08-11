export interface QuestionSetResponse {
  questionSetId: number;
  title: string;
  description: string;
  versionNumber: number;
  parentVersionId: number | null;
  isPublic: boolean;
  ownerNickname: string;
  questionMaps: QuestionMap[];
  tailQuestions: TailQuestionResponse[];
  tags: string[];
  createdAt: string;
  updatedAt: string;
}

export interface QuestionMap {
  mapId: number;
  questionId: number;
  answerId: number;
  displayOrder: number;
  question: QuestionResponse;
  answer: AnswerResponse;
}

export interface QuestionResponse {
  id: number;
  content: string;
  questionType: number;
  createdBy: string;
  createdAt: string;
}

export interface TailQuestionResponse {
  id: number;
  content: string;
  questionId: number;
  createdBy: string;
  userAnswer: string | null;
  displayOrder: number;
  createdAt: string;
  answeredAt: string | null;
}

export interface AnswerResponse {
  id: number;
  content: string;
  createdByNickname: string;
  createdAt: string;
}

export interface TagResponse {
  tag: string;
}

export interface Pageable {
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  offset: number;
  pageSize: number;
  pageNumber: number;
}

export interface GetQuestionSetsResponse {
  status: string;
  message: string;
  data: {
    content: QuestionSetResponse[];
    pageable: Pageable;
    totalElements: number;
    totalPages: number;
    last: boolean;
    size: number;
    number: number;
    first: boolean;
    numberOfElements: number;
    empty: boolean;
  };
}
