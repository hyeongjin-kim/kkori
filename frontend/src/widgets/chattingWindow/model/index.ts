import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { FormEvent } from 'react';
import { CHAT_TYPES } from '@/widgets/chattingWindow/model/chattingWindowType';
import useUserStore from '@/entities/user/model/useUserStore';

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
    sender: useUserStore.getState().nickname,
    timestamp,
    isMyMessage: true,
    confirmed: false,
  });
  usePracticeSessionStore
    .getState()
    .sendMessage(useUserStore.getState().nickname, text, timestamp);
};
