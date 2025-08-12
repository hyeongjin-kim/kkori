import { render, screen } from '@testing-library/react';
import DetailPageActions from '@/entities/questionSet/ui/DetailPageActions';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('DetailPageActions', () => {
  test('DetailPageActions 컴포넌트가 렌더링 된다.', () => {
    render(
      <MemoryRouterWrapped
        component={
          <DetailPageActions id="1" title="title" description="description" />
        }
      />,
    );
    expect(
      screen.getByRole('button', { name: 'import-question-set-button' }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole('link', { name: 'back-to-list-button' }),
    ).toBeInTheDocument();
  });
});
