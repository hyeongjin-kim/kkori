import { render, screen } from "@testing-library/react";
import MainLayout from "./MainLayout";

test("메인 레이아웃에서 헤더를 확인할할 수 있다.", () => {
    render(<MainLayout />);
    expect(screen.getByRole("banner")).toBeInTheDocument();
});