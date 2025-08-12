import useUserStore from '@/entities/user/model/useUserStore';
import { api } from '@/shared/api/api';

export const getMe = async () => {
  const response = await api.get('/api/users/me');
  useUserStore.getState().setNickname(response.data.data.nickname);
  useUserStore.getState().setId(response.data.data.id);
  return response.data;
};

export const postGuestLogin = async () => {
  const response = await api.post('/api/login/guest');
  return response.data;
};
