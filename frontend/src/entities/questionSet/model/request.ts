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
