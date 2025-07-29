import { FormEvent } from 'react';

const SubmitChatting = (e: FormEvent<HTMLFormElement>) => {
  e.preventDefault();
  const formData = new FormData(e.target as HTMLFormElement);
  const text = formData.get('text') as string;
  console.log(text);
};

export default SubmitChatting;
