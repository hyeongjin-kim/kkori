import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '../common/MemoryRouterWrapped';
import NavigationBar from './NavigationBar';
import { NAVIGATION_BAR_LINKS } from '../../constants';
import userEvent from '@testing-library/user-event';

describe('네비게이션 바', () => {
  test('네비게이션 바가 랜더링된다.', () => {
    render(<MemoryRouterWrapped component={<NavigationBar />} />);
    expect(screen.getByRole('navigation')).toBeInTheDocument();
  });

  test('네비게이션 바의 링크들이 랜더링된다.', () => {
    const links = Object.values(NAVIGATION_BAR_LINKS);
    render(<MemoryRouterWrapped component={<NavigationBar />} />);
    links.forEach(({ text }) => {
      expect(screen.getByRole('link', { name: text })).toBeInTheDocument();
    });
  });

  test('네비게이션 바의 링크를 클릭하면 해당 페이지로 이동한다.', () => {
    const links = Object.values(NAVIGATION_BAR_LINKS);
    render(<MemoryRouterWrapped component={<NavigationBar />} />);
    links.forEach(async ({ text }) => {
      const link = screen.getByRole('link', { name: text });
      await userEvent.click(link);
      expect(screen.getByRole('link', { name: text }));
    });
  });
});
