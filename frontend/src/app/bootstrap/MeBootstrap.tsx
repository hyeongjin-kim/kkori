import { useMe } from '@/features/auth/api/me';

export default function MeBootstrap() {
  useMe();

  return null;
}
