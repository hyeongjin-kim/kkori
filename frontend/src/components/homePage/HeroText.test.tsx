import { render, screen } from '@testing-library/react';
import HeroText from '@components/homePage/HeroText';

describe('HeroText', () => {
  test('HeroText가 렌더링 된다', () => {
    render(<HeroText />);
    expect(screen.getByLabelText('hero-text')).toBeInTheDocument();
  });
});
