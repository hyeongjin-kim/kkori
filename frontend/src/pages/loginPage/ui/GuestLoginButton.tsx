import { useNavigate } from 'react-router-dom';
import { useGuestLogin } from '@/features/auth/api/me';

function GuestLoginButton() {
  const navigate = useNavigate();
  const { mutate: guestLogin } = useGuestLogin();

  const handleClick = () => {
    guestLogin();
    navigate('/');
  };

  return (
    <button
      aria-label="guest-login-button"
      onClick={handleClick}
      className="w-full max-w-[180px] rounded-md bg-blue-500 px-4 py-3 text-sm font-semibold text-white shadow-sm transition-all duration-200 hover:bg-blue-600 focus:ring-2 focus:ring-blue-400 focus:ring-offset-1 focus:outline-none active:scale-[0.98]"
    >
      게스트 로그인
    </button>
  );
}

export default GuestLoginButton;
