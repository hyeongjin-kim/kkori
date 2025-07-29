import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';
import SoloPracticePage from '@pages/soloPracticePage/index';

describe('SoloPracticePage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<SoloPracticePage />} />);
  });

  test('SoloPracticePage 페이지가 렌더링 된다.', () => {
    expect(
      screen.getByRole('main', { name: 'solo-practice-page' }),
    ).toBeInTheDocument();
  });
});
