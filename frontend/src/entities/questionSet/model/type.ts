export interface QuestionSet {
  id: number;
  nickname: string;
  title: string;
  description: string;
  versionNumber: number;
  parentVersionId: number | null;
  isShared: boolean;
  tags: Tag[];
  questions: Question[];
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
