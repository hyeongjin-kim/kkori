import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import PairPracticePage from '@/pages/pairPracticePage';

describe('PairPracticePage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<PairPracticePage />} />);
  });

  test('PairPracticePage 페이지가 렌더링 된다.', () => {
    expect(
      screen.getByRole('main', { name: 'pair-practice-page' }),
    ).toBeInTheDocument();
  });
});
