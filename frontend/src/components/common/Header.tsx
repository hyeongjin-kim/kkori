import { Link } from 'react-router-dom';
import Logo from '../header/Logo';
import NavigationBar from '../header/NavigationBar';

function Header() {
  return (
    <header>
      <Logo />
      <NavigationBar />
      <Link to="/login">로그인</Link>
    </header>
  );
}

export default Header;
