import { render, screen } from '@testing-library/react';
import HeroText from '@/pages/homePage/ui/HeroText';

describe('HeroText', () => {
  test('HeroText가 렌더링 된다', () => {
    render(<HeroText />);
    expect(screen.getByLabelText('hero-text')).toBeInTheDocument();
  });
});
