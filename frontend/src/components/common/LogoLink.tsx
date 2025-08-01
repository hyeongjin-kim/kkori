import { Link } from 'react-router-dom';
import Logo from './Logo';

function LogoLink() {
  return (
    <Link to="/" aria-label="logo-link">
      <Logo />
    </Link>
  );
}

export default LogoLink;
