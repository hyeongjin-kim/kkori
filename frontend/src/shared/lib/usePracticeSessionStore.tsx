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

type PracticeSessionStore = InterviewSlice &
  ChattingWindowSlice &
  WebSocketSlice;

export const usePracticeSessionStore = create<PracticeSessionStore>()(
  (...args) => ({
    ...createInterviewSlice(...args),
    ...createChattingWindowSlice(...args),
    ...createWebSocketSlice(...args),
  }),
);
