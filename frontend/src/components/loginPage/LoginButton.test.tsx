jest.mock('./loginProvider');

import { render, screen } from '@testing-library/react';
import LoginButton from '@components/loginPage/LoginButton';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';
import userEvent from '@testing-library/user-event';
import loginProvider from '@components/loginPage/loginProvider';
const mockLoginProvider = loginProvider as jest.Mock;

describe('LoginButton', () => {
  beforeEach(() => {
    render(
      <MemoryRouterWrapped
        component={
          <LoginButton
            socialLoginRequestPath={'test-path'}
            socialLoginRequestText={'test-text'}
          />
        }
      />,
    );
  });

  test('소셜 로그인 종류를 전달하면 해당 소셜 로그인 버튼이 렌더링된다.', () => {
    const loginButton = screen.getByRole('button', { name: /test-text/i });
    expect(loginButton).toBeInTheDocument();
  });

  test('소셜 로그인 버튼을 누르면 해당 소셜 로그인 요청을 보낸다.', async () => {
    const loginButton = screen.getByRole('button', { name: /test-text/i });
    await userEvent.click(loginButton);
    expect(mockLoginProvider).toHaveBeenCalledWith('test-path');
  });
});
