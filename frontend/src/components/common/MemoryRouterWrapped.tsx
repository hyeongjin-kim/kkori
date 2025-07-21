import { MemoryRouter } from "react-router-dom";

function MemoryRouterWrapped({ component }: { component: React.ReactNode }) {
    return (
        <MemoryRouter>
            {component}
        </MemoryRouter>
    );
}

export default MemoryRouterWrapped;