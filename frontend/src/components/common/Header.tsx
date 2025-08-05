import NavigationBar from '@components/header/NavigationBar';
import GoToLoginButton from '@components/header/GoToLoginButton';
import LogoLink from './LogoLink';

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
