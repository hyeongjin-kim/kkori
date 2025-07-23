import { NAVIGATION_BAR_LINKS } from '../../constants';
import { Link } from 'react-router-dom';

function NavigationBar() {
  const links = Object.entries(NAVIGATION_BAR_LINKS);
  return (
    <nav>
      <ul>
        {links.map(([key, value]) => (
          <li key={key}>
            <Link to={value.path}>{value.text}</Link>
          </li>
        ))}
      </ul>
    </nav>
  );
}

export default NavigationBar;
