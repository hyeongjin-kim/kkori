import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';
import MyPage from '@pages/myPage/index';

describe('MyPage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<MyPage />} />);
  });

  test('MyPage 페이지가 렌더링 된다.', () => {
    expect(screen.getByRole('main', { name: 'my-page' })).toBeInTheDocument();
  });
});
