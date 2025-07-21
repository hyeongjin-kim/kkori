import { Link } from "react-router-dom";
import Logo from "../header/Logo";

function Header() {
    return (
        <header>
            <Logo />
            <nav>
                <ul>
                    <li><Link to="/">홈</Link></li>
                    <li><Link to="/interview-questions">면접 질문</Link></li>
                    <li><Link to="/my-page">마이페이지</Link></li>
                </ul>
            </nav>
            <Link to="/login">로그인</Link>
        </header>
    );
}

export default Header;