import { render, screen } from '@testing-library/react';
import BrowserRouterWrapped from '../../components/common/BrowserRouterWrapped';
import MyPage from './index';

describe('MyPage', () => {
  beforeEach(() => {
    render(<BrowserRouterWrapped component={<MyPage />} />);
  });

  test('MyPage 페이지가 렌더링 된다.', () => {
    expect(screen.getByRole('main', { name: 'my-page' })).toBeInTheDocument();
  });
});
