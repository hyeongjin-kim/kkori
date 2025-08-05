import { render, screen } from '@testing-library/react';
import LoginButton from '@/pages/loginPage/ui/LoginButton';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('LoginButton', () => {
  beforeEach(() => {
    render(
      <MemoryRouterWrapped
        component={
          <LoginButton
            onClick={() => {}}
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
});
