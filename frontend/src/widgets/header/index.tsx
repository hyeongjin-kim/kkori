import NavigationBar from '@/shared/ui/NavigationBar';
import GoToLoginButton from '@/features/auth/ui/GoToLoginButton';
import LogoLink from '@/widgets/header/ui/LogoLink';
import { useMe } from '@/features/auth/api/me';
import LoginButtonSkeleton from '@/widgets/header/ui/LoginButtonSkelton';
import UserMenu from '@/widgets/header/ui/UserMenu';
import { post } from '@/shared/api/api';

function Header() {
  const { data: me, isLoading } = useMe();
  return (
    <header className="text-text-white border-underline-gray flex min-h-16 items-center justify-between border-b px-32">
      <LogoLink />
      <NavigationBar />
      {isLoading ? (
        <LoginButtonSkeleton />
      ) : me ? (
        <UserMenu user={me} />
      ) : (
        <GoToLoginButton />
      )}
      <button
        onClick={() => {
          post('/api/admin/create-dummy-data');
        }}
      >
        더미 데이터 생성
      </button>
    </header>
  );
}

export default Header;
