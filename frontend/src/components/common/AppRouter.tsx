import { appRoutes } from '../../constants/routes';
import { Route, Routes } from 'react-router-dom';

function AppRouter() {
  return (
    <Routes>
      {appRoutes.map((route) => (
        <Route key={route.path} path={route.path} element={route.element} />
      ))}
    </Routes>
  );
}

export default AppRouter;
