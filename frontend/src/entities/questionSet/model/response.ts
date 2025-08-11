import { QuestionSetBase } from '@/entities/questionSet/model/request';

export interface QuestionSet extends QuestionSetBase {
  id: number;
  nickname: string;
  versionNumber: number;
  parentVersionId: number | null;
}

export interface Tag {
  id: number;
  tag: string;
}

export interface Question {
  id: number;
  content: string;
  questionType: 'DEFAULT' | 'CUSTOM';
  questionAnswer: string;
  displayOrder: number;
  tailQuestions: TailQuestion[];
}

export interface TailQuestion {
  id: number;
  content: string;
  userAnswer: string | null;
  questionOrder: number;
  generationContext: string;
}
