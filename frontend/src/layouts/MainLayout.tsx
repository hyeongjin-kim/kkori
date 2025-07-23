//메인 레이아웃에 있어야 할 것들

import { Outlet } from 'react-router-dom';
import Header from '../components/common/Header';

// 1. 헤더
function MainLayout() {
  return (
    <div>
      <Header />
      <main>
        <Outlet />
      </main>
    </div>
  );
}

export default MainLayout;
