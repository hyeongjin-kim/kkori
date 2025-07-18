import { render, screen } from "@testing-library/react";
import BrowserRouterWrapped from "../../components/common/BrowserRouterWrapped";
import SoloPracticePage from "./index";

test("SoloPracticePage 페이지가 렌더링 된다.", () => {
    render(<BrowserRouterWrapped component={<SoloPracticePage />} />);
    expect(screen.getByText("SoloPracticePage")).toBeInTheDocument();
});






    