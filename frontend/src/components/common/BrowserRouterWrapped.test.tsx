import { render, screen } from "@testing-library/react";
import BrowserRouterWrapped from "./BrowserRouterWrapped";

test("어떤 컴포넌트를 전달하면 이를 BrowserRouter로 감싼 컴포넌트가 렌더링 된다.", () => {
    const Component = () => {
        return <button>Component</button>;
    };
    render(<BrowserRouterWrapped component={<Component />} />);
    expect(screen.getByRole("button", { name: "Component" })).toBeInTheDocument();
});







