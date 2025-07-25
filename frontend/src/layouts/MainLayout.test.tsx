import { render, screen } from '@testing-library/react';
import MainLayout from '@layouts/MainLayout';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';

test('메인 레이아웃에서 헤더를 확인할 수 있다.', () => {
  render(<MemoryRouterWrapped component={<MainLayout />} />);
  expect(screen.getByRole('banner')).toBeInTheDocument();
});
