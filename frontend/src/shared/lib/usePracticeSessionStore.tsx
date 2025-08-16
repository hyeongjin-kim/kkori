import { create } from 'zustand';
import {
  createInterviewSlice,
  InterviewSlice,
} from '@/widgets/interviewSection/model/interviewSlice';
import { createWebSocketSlice, WebSocketSlice } from './webSocketSlice';
import {
  ChattingWindowSlice,
  createChattingWindowSlice,
} from '@/widgets/chattingWindow/model/chattingWindowSlice';
import { createJSONStorage, persist } from 'zustand/middleware';

type PracticeSessionStore = InterviewSlice &
  ChattingWindowSlice &
  WebSocketSlice;

export const usePracticeSessionStore = create<PracticeSessionStore>()(
  persist(
    (set, get, api) => ({
      ...createInterviewSlice(set, get, api),
      ...createChattingWindowSlice(set, get, api),
      ...createWebSocketSlice(set, get, api),
    }),
    {
      name: 'practiceSessionStore',
      storage: createJSONStorage(() => localStorage),
      partialize: state => {
        const { roomId, questionSetId, opponentNickname, joinRoomMode } = state;
        return {
          roomId,
          questionSetId,
          opponentNickname,
          joinRoomMode,
        };
      },
      version: 1,
    },
  ),
);
