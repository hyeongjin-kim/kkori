import { fireEvent, render, screen } from "@testing-library/react";
import MemoryRouterWrapped from "../common/MemoryRouterWrapped";
import Logo from "./Logo";

test("logo 컴포넌트가 렌더링 된다.", () => {
    render(<MemoryRouterWrapped component={<Logo />} />);
    expect(screen.getByRole("img", { name: "로고 이미지" })).toBeInTheDocument();
});

test("로고를 클릭하면 메인 페이지로 이동한다.", () => {
    render(<MemoryRouterWrapped component={<Logo />} />);
    const logo = screen.getByRole("img", { name: "로고 이미지" });
    fireEvent.click(logo);
    expect(window.location.pathname).toBe("/");
});