import { Link } from "react-router-dom";

function Header() {
    return (
        <header>
            <img src="/logo.png" alt="로고 이미지" />
            <nav>
                <Link to="/">홈</Link>
                <Link to="/interview-questions">면접 질문</Link>
                <Link to="/my-page">마이페이지</Link>
            </nav>
            <button>로그인</button>
        </header>
    );
}

export default Header;