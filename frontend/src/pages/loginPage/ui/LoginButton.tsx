interface LoginButtonProps {
  onClick: () => void;
  socialLoginRequestText: string;
}

function LoginButton({ onClick, socialLoginRequestText }: LoginButtonProps) {
  return (
    <button
      className="w-full rounded border bg-white px-4 py-2 text-sm font-medium text-black hover:bg-gray-100 active:bg-gray-200"
      onClick={onClick}
    >
      {socialLoginRequestText}
    </button>
  );
}

export default LoginButton;
