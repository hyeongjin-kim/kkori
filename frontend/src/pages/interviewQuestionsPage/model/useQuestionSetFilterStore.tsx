import { create } from 'zustand';
import { QuestionSet, Tag } from '@/entities/questionSet/model/type';
import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';

interface QuestionSetFilterState {
  selectedTag: Tag;
  questionSets: QuestionSet[];
}

interface QuestionSetFilterAction {
  setSelectedTag: (tag: Tag) => void;
  setQuestionSets: (questionSets: QuestionSet[]) => void;
}

const initialState: QuestionSetFilterState = {
  selectedTag: TAG_FILTER_LIST[0],
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
