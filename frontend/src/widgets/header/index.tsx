import NavigationBar from '@/shared/ui/NavigationBar';
import GoToLoginButton from '@/features/auth/ui/GoToLoginButton';
import LogoLink from '@/widgets/header/ui/LogoLink';
import { useMe } from '@/features/auth/api/me';
import LoginButtonSkeleton from '@/widgets/header/ui/LoginButtonSkelton';
import UserMenu from '@/widgets/header/ui/UserMenu';

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
    </header>
  );
}

export default Header;
