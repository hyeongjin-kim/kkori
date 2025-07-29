import { Outlet } from 'react-router-dom';
import Header from '@components/common/Header';

function MainLayout() {
  return (
    <div className="bg-background flex h-full min-h-screen flex-col">
      <Header />
      <Outlet />
    </div>
  );
}

export default MainLayout;
