import { create } from 'zustand';

type Toast = { id: number; message: string };
const useInterviewToastStore = create<{
  toasts: Toast[];
  addToast: (msg: string) => void;
  removeToast: (id: number) => void;
}>(set => ({
  toasts: [],
  addToast: msg =>
    set(state => ({
      toasts: [...state.toasts, { id: Date.now(), message: msg }],
    })),
  removeToast: id =>
    set(state => ({
      toasts: state.toasts.filter(t => t.id !== id),
    })),
}));

export default useInterviewToastStore;
