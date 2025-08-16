import { Outlet } from 'react-router-dom';
import Header from '@/widgets/header';
import { getMe } from '@/features/auth/api/request';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

function ProtectedLayout() {
  const navigate = useNavigate();
  useEffect(() => {
    getMe().catch(() => {
      navigate('/login');
      toast.error('로그인 후 이용 가능합니다.');
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
