import loginProvider from './loginProvider';

function LoginButton({
  socialLoginRequestPath,
  socialLoginRequestText,
}: {
  socialLoginRequestPath: string;
  socialLoginRequestText: string;
}) {
  return (
    <button
      className="w-full px-4 py-2 rounded border text-sm font-medium text-black bg-white hover:bg-gray-100 active:bg-gray-200"
      onClick={() => {
        loginProvider(socialLoginRequestPath);
      }}
    >
      {socialLoginRequestText}
    </button>
  );
}

export default LoginButton;
