import { Link } from 'react-router-dom';
import Logo from '@components/common/Logo';
import NavigationBar from '@components/header/NavigationBar';
import GoToLoginButton from '@components/header/GoToLoginButton';

function Header() {
  return (
    <header className="text-text-white border-underline-gray flex min-h-16 items-center justify-between border-b px-32">
      <Logo className="mt-1 -ml-3 h-8 w-8" />
      <NavigationBar />
      <GoToLoginButton />
    </header>
  );
}

export default Header;
