import { render, screen } from "@testing-library/react";
import BrowserRouterWrapped from "../../components/common/BrowserRouterWrapped";
import PairPracticePage from "./index";

test("PairPracticePage 페이지가 렌더링 된다.", () => {
    render(<BrowserRouterWrapped component={<PairPracticePage />} />);
    expect(screen.getByText("PairPracticePage")).toBeInTheDocument();
});






    