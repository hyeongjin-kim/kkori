import { render, screen } from '@testing-library/react';
import LoginButton from '@/pages/loginPage/ui/LoginButton';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('LoginButton', () => {
  beforeEach(() => {
    render(
      <MemoryRouterWrapped component={<LoginButton onClick={() => {}} />} />,
    );
  });

  test('카카오 로그인 버튼이 렌더링 된다.', () => {
    const loginButton = screen.getByRole('button', {
      name: /oauth-login-button/i,
    });
    expect(loginButton).toBeInTheDocument();
  });
});
