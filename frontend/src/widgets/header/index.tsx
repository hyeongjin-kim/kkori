import NavigationBar from '@/shared/ui/NavigationBar';
import GoToLoginButton from '@/features/auth/ui/GoToLoginButton';
import LogoLink from '@/widgets/header/ui/LogoLink';

function Header() {
  return (
    <header className="text-text-white border-underline-gray flex min-h-16 items-center justify-between border-b px-32">
      <LogoLink />
      <NavigationBar />
      <GoToLoginButton />
    </header>
  );
}

export default Header;
