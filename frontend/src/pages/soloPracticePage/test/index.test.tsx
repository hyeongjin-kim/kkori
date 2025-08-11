import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import SoloPracticePage from '@/pages/soloPracticePage/page/index';

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
