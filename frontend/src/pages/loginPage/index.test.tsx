import { render, screen } from "@testing-library/react";
import BrowserRouterWrapped from "../../components/common/BrowserRouterWrapped";
import LoginPage from "./index";

test("LoginPage 페이지가 렌더링 된다.", () => {
    render(<BrowserRouterWrapped component={<LoginPage />} />);
    expect(screen.getByText("LoginPage")).toBeInTheDocument();
});






    