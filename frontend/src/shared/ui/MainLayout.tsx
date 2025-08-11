import { Outlet } from 'react-router-dom';
import Header from '@/widgets/header';

function MainLayout() {
  return (
    <div className="bg-background relative flex h-full min-h-screen flex-col">
      <Header />
      <Outlet />
    </div>
  );
}

export default MainLayout;
