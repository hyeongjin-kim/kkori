import { render, screen } from '@testing-library/react';
import GoToButton from '@/shared/ui/GoToButton';

describe('GoToButton', () => {
  test('GoToButton 컴포넌트가 렌더링 된다.', () => {
    render(<GoToButton to="/" label="go-to-button" text="test" />);
    expect(
      screen.getByRole('link', { name: 'go-to-button' }),
    ).toBeInTheDocument();
  });
});
