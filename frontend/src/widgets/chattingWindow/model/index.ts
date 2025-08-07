import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { FormEvent } from 'react';
import { CHAT_TYPES } from '@/widgets/chattingWindow/model/chattingWindowType';

export const submitChatting = (e: FormEvent<HTMLFormElement>) => {
  e.preventDefault();
  const formData = new FormData(e.target as HTMLFormElement);
  if (formData.get('text') === '') return;
  e.currentTarget.reset();
  const text = formData.get('text') as string;
  const id = Date.now().toString();
  //TODO: 스크롤 최하단으로 유지하는 기능 추가
  usePracticeSessionStore.getState().addMessage({
    id,
    type: CHAT_TYPES.chat,
    text,
    sender: 'me', // TODO: 유저 이름 받아오기
    timestamp: new Date().toISOString(),
    isMyMessage: true,
    confirmed: false,
  });
};
