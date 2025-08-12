export interface QuestionSetRequest {
  title: string;
  description?: string;
  tags?: string[];
  questions: QuestionRequest[];
}

export interface QuestionRequest {
  content: string;
  questionType: number;
  expectedAnswer: string;
  tailQuestions?: TailQuestionRequest[];
}

export interface TailQuestionRequest {
  content: string;
}

export interface CreateQuestionSetRequest {
  title: string;
  description?: string;
  tags?: string[];
  questions: QuestionRequest[];
}

export type GetQuestionSetsParams = {
  page?: number;
  size?: number;
  sort?: string;
  createdBy?: string;
  isPublic?: boolean;
  tags?: string[];
};

export type GetMyQuestionSetsParams = {
  page?: number;
  size?: number;
};

export interface CopyQuestionSetRequest {
  originalQuestionSetId: number;
  title: string;
  description: string;
  copyTags?: boolean;
}

export interface UpdateQuestionSet {
  content: string;
  questionType: number;
  expectedAnswer: string;
  displayOrder: number;
}

export interface UpdateQuestionSetRequest {
  questions: UpdateQuestionSet[];
  tags?: string[];
}

export interface UpdateQuestionSetMetadataRequest {
  title?: string;
  description?: string;
  isPublic?: boolean;
}
