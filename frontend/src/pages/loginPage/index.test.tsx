import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '../../components/common/MemoryRouterWrapped';
import LoginPage from './index';

describe('LoginPage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<LoginPage />} />);
  });

  test('LoginPage 페이지가 렌더링 된다.', () => {
    expect(
      screen.getByRole('main', { name: 'login-page' }),
    ).toBeInTheDocument();
  });
});
