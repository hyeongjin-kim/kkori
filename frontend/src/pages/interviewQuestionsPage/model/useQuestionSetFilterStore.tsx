import { create } from 'zustand';
import { QuestionSet, Tag } from '@/entities/questionSet/model/type';

interface QuestionSetFilterState {
  selectedTag: Tag | null;
  questionSets: QuestionSet[];
}

interface QuestionSetFilterAction {
  setSelectedTag: (tag: Tag) => void;
  setQuestionSets: (questionSets: QuestionSet[]) => void;
}

const initialState: QuestionSetFilterState = {
  selectedTag: null,
  questionSets: [],
};

const useQuestionSetFilterStore = create<
  QuestionSetFilterState & QuestionSetFilterAction
>(set => ({
  ...initialState,
  setSelectedTag: tag => set({ selectedTag: tag }),
  setQuestionSets: questionSets => set({ questionSets }),
}));

export default useQuestionSetFilterStore;
