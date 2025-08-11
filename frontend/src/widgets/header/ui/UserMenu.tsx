import { useQueryClient } from '@tanstack/react-query';

interface UserMenuProps {
  user: {
    data: {
      nickname: string;
    };
  };
}

function UserMenu({ user }: UserMenuProps) {
  const qc = useQueryClient();

  const handleLogout = async () => {
    qc.setQueryData(['me'], null);
  };

  return (
    <div aria-label="user-menu" className="flex items-center gap-4">
      <span className="text-sm font-bold text-gray-600">
        {user.data.nickname}님
      </span>
      <button
        onClick={handleLogout}
        aria-label="logout-button"
        className="focus-visible:ring-point-400 inline-flex h-8 items-center gap-2 rounded-md bg-white px-4 text-sm text-gray-900 shadow-sm ring-1 ring-gray-200 transition hover:bg-gray-50 focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 active:scale-[0.99] disabled:opacity-50"
      >
        로그아웃
      </button>
    </div>
  );
}

export default UserMenu;
