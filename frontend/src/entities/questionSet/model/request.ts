import { Question, Tag } from '@/entities/questionSet/model/response';

export interface QuestionSetBase {
  nickname: string;
  title: string;
  description: string;
  isShared: boolean;
  tags: Tag[];
  questions: Question[];
}

export interface CreateQuestionSetRequest extends QuestionSetBase {}
