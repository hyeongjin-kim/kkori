import { render, screen } from '@testing-library/react';
import DetailPageActions from '@/entities/questionSet/ui/DetailPageActions';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import { toOverviewVM } from '@/entities/questionSet/model/toOverviewVM';
import { questionSetList } from '@/entities/questionSet/model/mock';

describe('DetailPageActions', () => {
  test('DetailPageActions 컴포넌트가 렌더링 된다.', () => {
    render(
      <MemoryRouterWrapped
        component={<DetailPageActions vm={toOverviewVM(questionSetList[0])} />}
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
