import { appRoutes } from '../../constants/routes';
import { Route, Routes } from 'react-router-dom';
import MainLayout from '../../layouts/MainLayout';

function AppRouter() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        {appRoutes.mainLayout.map(route => (
          <Route key={route.path} path={route.path} element={route.element} />
        ))}
      </Route>
    </Routes>
  );
}

export default AppRouter;
