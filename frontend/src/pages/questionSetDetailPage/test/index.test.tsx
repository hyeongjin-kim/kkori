import { render, screen } from '@testing-library/react';
import QuestionSetDetailPage from '@/pages/questionSetDetailPage/page';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('QuestionSetDetailPage', () => {
  test('QuestionSetDetailPage가 렌더링 된다.', () => {
    render(
      <MemoryRouterWrapped
        component={<QuestionSetDetailPage />}
        initialEntries={['/question-set-detail/1']}
      />,
    );
    expect(
      screen.getByRole('main', { name: 'question-set-detail-page' }),
    ).toBeInTheDocument();
  });
});
