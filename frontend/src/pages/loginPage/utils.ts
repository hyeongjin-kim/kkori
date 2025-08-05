import { get } from '@/api/api';

export async function redirectToSocialLogin(socialLoginRequestPath: string) {
  const response = await get<string>(socialLoginRequestPath);
  window.location.href = response;
}
