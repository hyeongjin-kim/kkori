import { Outlet } from 'react-router-dom';
import Header from '@/widgets/header';
import { getMe } from '@/features/auth/api/request';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function ProtectedLayout() {
  const navigate = useNavigate();
  useEffect(() => {
    getMe().catch(() => {
      navigate('/login');
    });
  }, []);

  return (
    <div
      aria-label="protected-layout"
      className="bg-background relative flex h-full min-h-screen flex-col"
    >
      <Header />
      <Outlet />
    </div>
  );
}

export default ProtectedLayout;
