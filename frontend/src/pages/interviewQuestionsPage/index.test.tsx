import { render, screen } from "@testing-library/react";
import BrowserRouterWrapped from "../../components/common/BrowserRouterWrapped";
import InterviewQuestionsPage from "./index";

test("InterviewQuestionsPage 페이지가 렌더링 된다.", () => {
    render(<BrowserRouterWrapped component={<InterviewQuestionsPage />} />);
    expect(screen.getByText("InterviewQuestionsPage")).toBeInTheDocument();
});






    