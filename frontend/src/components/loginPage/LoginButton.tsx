import SocialLoginRequestProps from '@components/loginPage/SocialLoginRequestProps';
import loginProvider from '@components/loginPage/loginProvider';
import { get } from '@api/api';

function LoginButton({
  socialLoginRequestPath,
  socialLoginRequestText,
}: SocialLoginRequestProps) {
  return (
    <button
      className="w-full rounded border bg-white px-4 py-2 text-sm font-medium text-black hover:bg-gray-100 active:bg-gray-200"
      onClick={() => {
        loginProvider(socialLoginRequestPath);
      }}
    >
      {socialLoginRequestText}
    </button>
  );
}

export default LoginButton;
