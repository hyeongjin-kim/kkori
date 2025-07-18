import { render, screen } from "@testing-library/react";
import BrowserRouterWrapped from "../../components/common/BrowserRouterWrapped";
import MyPage from "./index";

test("MyPage 페이지가 렌더링 된다.", () => {
    render(<BrowserRouterWrapped component={<MyPage />} />);
    expect(screen.getByText("MyPage")).toBeInTheDocument();
});






    