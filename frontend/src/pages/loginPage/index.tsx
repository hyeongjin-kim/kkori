import LoginButton from '@components/loginPage/LoginButton';
import { SOCIAL_LOGIN_REQUEST_PATHS } from '@constants/index';
import { redirectToSocialLogin } from '@pages/loginPage/utils';

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
