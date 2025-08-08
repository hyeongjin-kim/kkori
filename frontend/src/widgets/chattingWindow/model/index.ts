import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { FormEvent } from 'react';
import { CHAT_TYPES } from '@/widgets/chattingWindow/model/chattingWindowType';

export const submitChatting = (e: FormEvent<HTMLFormElement>) => {
  e.preventDefault();
  const formData = new FormData(e.target as HTMLFormElement);
  if (formData.get('text') === '') return;
  e.currentTarget.reset();
  const text = formData.get('text') as string;
  const timestamp = new Date().getTime();
  usePracticeSessionStore.getState().addMessage({
    type: CHAT_TYPES.chat,
    content: text,
    sender: 'me', // TODO: 유저 이름 받아오기
    timestamp,
    isMyMessage: true,
    confirmed: false,
  });
};
