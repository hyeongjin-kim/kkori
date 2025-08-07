import { render, screen } from '@testing-library/react';
import QuestionSetCreatePage from '@/pages/questionSetCreatePage/page';

describe('QuestionSetCreatePage', () => {
  test('QuestionSetCreatePage가 렌더링 된다.', () => {
    render(<QuestionSetCreatePage />);
    expect(
      screen.getByRole('main', { name: 'question-set-create-page' }),
    ).toBeInTheDocument();
  });
});
