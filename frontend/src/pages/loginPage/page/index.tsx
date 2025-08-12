import LoginButton from '@/pages/loginPage/ui/LoginButton';
import { SOCIAL_LOGIN_REQUEST_PATHS } from '@/features/auth/model/constants';
import { redirectToSocialLogin } from '@/pages/loginPage/utils';
import AuthTitleCard from '@/pages/loginPage/ui/AuthTitleCard';
import GuestLoginButton from '@/pages/loginPage/ui/GuestLoginButton';

function LoginPage() {
  return (
    <main
      aria-label="login-page"
      className="mt-20 flex h-50 items-center justify-center bg-gray-50 px-4"
    >
      <AuthTitleCard
        title="로그인하기"
        description="간편 로그인으로 바로 시작하세요."
      >
        <div className="flex w-80 flex-col items-center gap-2">
          {Object.values(SOCIAL_LOGIN_REQUEST_PATHS).map(({ path }) => (
            <LoginButton
              key={path}
              onClick={() => redirectToSocialLogin(path)}
            />
          ))}

          <GuestLoginButton />
        </div>
      </AuthTitleCard>
    </main>
  );
}

export default LoginPage;
