import { create } from 'zustand';
import { QuestionSet } from '@/entities/questionSet/model/response';
import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';
import { questionSetList } from '@/entities/questionSet/model/mock';

interface QuestionSetFilterState {
  selectedTag: string;
  questionSets: QuestionSet[];
}

interface QuestionSetFilterAction {
  setSelectedTag: (tag: string) => void;
  setQuestionSets: (questionSets: QuestionSet[]) => void;
}

const initialState: QuestionSetFilterState = {
  selectedTag: TAG_FILTER_LIST[0].tag,
  questionSets: questionSetList,
};

const useQuestionSetFilterStore = create<
  QuestionSetFilterState & QuestionSetFilterAction
>(set => ({
  ...initialState,
  setSelectedTag: tag => set({ selectedTag: tag }),
  setQuestionSets: questionSets => set({ questionSets }),
}));

export default useQuestionSetFilterStore;
