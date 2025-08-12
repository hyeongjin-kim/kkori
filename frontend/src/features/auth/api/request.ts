import { api } from '@/shared/api/api';

export const getMe = async () => {
  const response = await api.get('/api/users/me');
  return response.data;
};

export const postGuestLogin = async () => {
  const response = await api.post('/api/login/guest');
  return response.data;
};
