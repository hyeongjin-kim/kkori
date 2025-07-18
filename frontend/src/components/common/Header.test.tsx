import { fireEvent, render, screen } from "@testing-library/react";
import Header from "./Header";
import BrowserRouterWrapped from "./BrowserRouterWrapped";

test("헤더에 로고가 렌더링 된다.", () => {
    render(<BrowserRouterWrapped component={<Header />} />);
    expect(screen.getByRole("img", { name: "로고 이미지" })).toBeInTheDocument();
}); 

describe("네비게이션 바", () => {
    test("텍스트가 렌더링 된다.", () => {
        render(<BrowserRouterWrapped component={<Header />} />);
        expect(screen.getByRole("link", { name: "홈" })).toBeInTheDocument();
    });
    test("마이 페이지 링크를 클릭하면 마이 페이지로 이동한다.", () => {
        render(<BrowserRouterWrapped component={<Header />} />);
        const myPageLink = screen.getByRole("link", { name: "마이페이지" });
        fireEvent.click(myPageLink);
        expect(window.location.pathname).toBe("/my-page");
    });
});


describe("로그인 버튼", () => {
    test("로그인 버튼이 렌더링 된다.", () => {
        render(<BrowserRouterWrapped component={<Header />} />);
        expect(screen.getByRole("link", { name: "로그인" })).toBeInTheDocument();
    });
    
    test("로그인 버튼을 클릭하면 로그인 페이지로 이동한다.", () => {
        render(<BrowserRouterWrapped component={<Header />} />);
        const loginButton = screen.getByRole("link", { name: "로그인" });
        fireEvent.click(loginButton);
        expect(window.location.pathname).toBe("/login");
    });
}); 
