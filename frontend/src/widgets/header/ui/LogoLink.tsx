import { Link } from 'react-router-dom';
import Logo from '../../../shared/ui/Logo';

function LogoLink() {
  return (
    <Link to="/" aria-label="logo-link">
      <Logo className="mt-1 -ml-3 h-12 w-auto" />
    </Link>
  );
}

export default LogoLink;
