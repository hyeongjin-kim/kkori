// features/auth/api/me.ts
import { useQuery } from '@tanstack/react-query';
import { getMe } from '@/features/auth/api/request';

const FIVE_MINUTES_IN_MS = 1000 * 60 * 5;

export function useMe() {
  return useQuery({
    queryKey: ['me'],
    queryFn: getMe,
    staleTime: FIVE_MINUTES_IN_MS,
    retry: false,
  });
}
