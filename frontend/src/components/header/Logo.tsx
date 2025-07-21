import { Link } from "react-router-dom";

function Logo() {
    return (
        <Link to="/">
            <img src="/Logo.svg" alt="로고 이미지" role="img" className="w-40 aspect-auto" />
        </Link>
    );
}

export default Logo;