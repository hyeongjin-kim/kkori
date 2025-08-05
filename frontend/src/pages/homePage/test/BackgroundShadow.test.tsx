import { render, screen } from '@testing-library/react';
import BackgroundShadow from '@/pages/homePage/ui/BackgroundShadow';

describe('BackgroundShadow', () => {
  test('백그라운드 쉐도우가 렌더링 된다', () => {
    render(<BackgroundShadow />);
    expect(screen.getByLabelText('background-shadow')).toBeInTheDocument();
  });
});
