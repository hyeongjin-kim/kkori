import { api } from '@/shared/api/api';

export const getMe = async () => {
  const response = await api.get('/api/users/me');
  return response.data;
};
