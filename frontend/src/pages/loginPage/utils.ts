import { get } from '@/shared/api/api';

export async function redirectToSocialLogin(socialLoginRequestPath: string) {
  const response = await get<string>(socialLoginRequestPath);
  window.location.href = response;
}
