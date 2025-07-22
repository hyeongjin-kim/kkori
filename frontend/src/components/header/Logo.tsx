import { Link } from 'react-router-dom';

function Logo() {
  return (
    <Link to="/">
      <img
        src="/Logo.svg"
        alt="로고 이미지"
        role="img"
        className="aspect-auto w-40"
      />
    </Link>
  );
}

export default Logo;
