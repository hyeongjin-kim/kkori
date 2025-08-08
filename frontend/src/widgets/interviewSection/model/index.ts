import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';

export function switchScreen() {
  console.log('switchScreen');
}

export function startAnswer() {
  console.log('startAnswer');
}

export function endAnswer() {
  console.log('endAnswer');
}

export function endInterview() {
  console.log('endInterview');
}

export function startInterview() {
  usePracticeSessionStore.getState().interviewStart();
}
