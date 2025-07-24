import LoginButton from '../../components/loginPage/LoginButton';

import { SOCIAL_LOGIN_REQUEST_PATHS } from '../../constants';

function LoginPage() {
  return (
    <main aria-label="login-page">
      {Object.values(SOCIAL_LOGIN_REQUEST_PATHS).map(({ path, text }) => (
        <LoginButton
          key={path}
          socialLoginRequestPath={path}
          socialLoginRequestText={text}
        />
      ))}
    </main>
  );
}

export default LoginPage;
