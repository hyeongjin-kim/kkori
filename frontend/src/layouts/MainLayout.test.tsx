import { render, screen } from '@testing-library/react';
import MainLayout from './MainLayout';
import BrowserRouterWrapped from '../components/common/BrowserRouterWrapped';

test('메인 레이아웃에서 헤더를 확인할 수 있다.', () => {
  render(<BrowserRouterWrapped component={<MainLayout />} />);
  expect(screen.getByRole('banner')).toBeInTheDocument();
});
