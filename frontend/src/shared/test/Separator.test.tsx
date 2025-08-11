import Separator from '@/shared/ui/Separator';
import { render, screen } from '@testing-library/react';

describe('Separator', () => {
  test('Separator 컴포넌트가 렌더링 된다.', () => {
    render(<Separator />);
    expect(screen.getByRole('separator')).toBeInTheDocument();
  });
});
