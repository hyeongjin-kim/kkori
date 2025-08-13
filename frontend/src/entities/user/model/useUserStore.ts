import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

interface UserState {
  nickname: string;
  userId: number;
}

interface UserAction {
  setNickname: (nickname: string) => void;
  setUserId: (userId: number) => void;
}

const initialState: UserState = {
  nickname: '',
  userId: 0,
};

const useUserStore = create<UserState & UserAction>()(
  persist(
    set => ({
      ...initialState,
      setNickname: (nickname: string) => set({ nickname }),
      setUserId: (userId: number) => set({ userId }),
    }),
    {
      name: 'user',
      storage: createJSONStorage(() => localStorage),
    },
  ),
);

export default useUserStore;
