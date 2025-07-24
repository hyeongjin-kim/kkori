import { BrowserRouter } from 'react-router-dom';

function BrowserRouterWrapped({ component }: { component: React.ReactNode }) {
  return <BrowserRouter>{component}</BrowserRouter>;
}

export default BrowserRouterWrapped;
