import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { getMe, postGuestLogin } from '@/features/auth/api/request';

const FIVE_MINUTES_IN_MS = 1000 * 60 * 5;

export function useMe() {
  return useQuery({
    queryKey: ['me'],
    queryFn: getMe,
    staleTime: FIVE_MINUTES_IN_MS,
    retry: false,
  });
}

export function useGuestLogin() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: postGuestLogin,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['me'] });
    },
  });
}
