import { create } from 'zustand';

export type PracticeType = 'pre-interview' | 'interview';

interface PracticeState {
  status: PracticeType;
}

interface PracticeActions {
  setStatus: (status: PracticeType) => void;
}

const initialState: PracticeState = {
  status: 'pre-interview',
};

const usePracticeStore = create<PracticeState & PracticeActions>(
  (set, get) => ({
    ...initialState,
    setStatus: status => set({ status }),
  }),
);

export default usePracticeStore;
