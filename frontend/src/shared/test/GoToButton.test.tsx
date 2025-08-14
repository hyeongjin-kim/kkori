import { render, screen } from '@testing-library/react';
import GoToButton from '@/shared/ui/GoToButton';
import userEvent from '@testing-library/user-event';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('GoToButton', () => {
  beforeEach(() => {
    render(
      <MemoryRouterWrapped
        component={<GoToButton to="/login" label="go-to-button" text="test" />}
      />,
    );
  });
  test('GoToButton 컴포넌트가 렌더링 된다.', () => {
    expect(
      screen.getByRole('link', { name: 'go-to-button' }),
    ).toBeInTheDocument();
  });

  test('GoToButton 컴포넌트를 클릭하면 해당 경로로 이동한다.', async () => {
    const button = screen.getByRole('link', { name: 'go-to-button' });
    await userEvent.click(button);
    expect(
      screen.getByRole('main', { name: 'login-page' }),
    ).toBeInTheDocument();
  });
});
