import { create } from 'zustand';

interface UserState {
  nickname: string;
  id: string;
}

interface UserAction {
  setNickname: (nickname: string) => void;
  setId: (id: string) => void;
}

const initialState: UserState = {
  nickname: '',
  id: '',
};

const useUserStore = create<UserState & UserAction>(set => ({
  ...initialState,
  setNickname: (nickname: string) => set({ nickname }),
  setId: (id: string) => set({ id }),
}));

export default useUserStore;
