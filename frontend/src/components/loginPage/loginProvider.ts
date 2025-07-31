import { get } from '@api/api';

async function loginProvider(socialLoginRequestPath: string) {
  const response = await get<string>(socialLoginRequestPath);
  window.location.href = response;
}

export default loginProvider;
