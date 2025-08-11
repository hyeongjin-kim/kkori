import { render, screen } from '@testing-library/react';
import QuestionSetCreatePage from '@/pages/questionSetCreatePage/page';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('QuestionSetCreatePage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<QuestionSetCreatePage />} />);
  });
  test('QuestionSetCreatePage가 렌더링 된다.', () => {
    expect(
      screen.getByRole('main', { name: 'question-set-create-page' }),
    ).toBeInTheDocument();
  });

  test('QuestionSetForm이 렌더링 된다.', () => {
    expect(
      screen.getByRole('region', { name: 'question-set-form' }),
    ).toBeInTheDocument();
  });
});
