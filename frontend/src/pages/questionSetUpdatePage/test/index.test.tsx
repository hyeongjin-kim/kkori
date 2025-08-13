import { render, screen } from '@testing-library/react';
import QuestionSetUpdatePage from '@/pages/questionSetUpdatePage/page';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('QuestionSetUpdatePage', () => {
  test('QuestionSetUpdatePage가 렌더링 된다.', () => {
    render(<MemoryRouterWrapped component={<QuestionSetUpdatePage />} />);
    expect(
      screen.getByRole('main', { name: 'question-set-update-page' }),
    ).toBeInTheDocument();
  });
});
