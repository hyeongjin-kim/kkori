import { render, screen } from '@testing-library/react';
import Alert from '@/shared/ui/Alert';

describe('Alert', () => {
  test('Alert 컴포넌트가 렌더링 된다.', () => {
    render(<Alert>test</Alert>);
    expect(screen.getByRole('alert')).toBeInTheDocument();
  });
});
