import useChattingWindowStore from '@/stores/useChattingWindowStore';
import { FormEvent } from 'react';

const SubmitChatting = (e: FormEvent<HTMLFormElement>) => {
  e.preventDefault();
  const formData = new FormData(e.target as HTMLFormElement);
  if (formData.get('text') === '') return;
  e.currentTarget.reset();
  const text = formData.get('text') as string;
  const id = Date.now().toString();
  //TODO: 스크롤 최하단으로 유지하는 기능 추가
  useChattingWindowStore.getState().addMessage({
    id,
    type: 'user',
    message: text,
    sender: 'me', // TODO: 유저 이름 받아오기
  });
};

export default SubmitChatting;
