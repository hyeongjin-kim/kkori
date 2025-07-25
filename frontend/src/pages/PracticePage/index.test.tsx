import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';
import PracticePage from '@pages/PracticePage/index';

describe('PracticePage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<PracticePage />} />);
  });

  test('PracticePage 페이지가 렌더링 된다.', () => {
    expect(
      screen.getByRole('main', { name: 'practice-page' }),
    ).toBeInTheDocument();
  });
});
