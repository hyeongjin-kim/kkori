import LoginButton from '@/pages/loginPage/ui/LoginButton';
import { SOCIAL_LOGIN_REQUEST_PATHS } from '@/features/auth/model/constants';
import { redirectToSocialLogin } from '@/pages/loginPage/utils';

function LoginPage() {
  return (
    <main aria-label="login-page">
      {Object.values(SOCIAL_LOGIN_REQUEST_PATHS).map(({ path, text }) => (
        <LoginButton
          key={path}
          onClick={() => redirectToSocialLogin(path)}
          socialLoginRequestText={text}
        />
      ))}
    </main>
  );
}

export default LoginPage;
