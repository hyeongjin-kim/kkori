import { appRoutes } from '@/app/model/routes';
import { Route, Routes } from 'react-router-dom';
import MainLayout from '@/shared/ui/MainLayout';

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
