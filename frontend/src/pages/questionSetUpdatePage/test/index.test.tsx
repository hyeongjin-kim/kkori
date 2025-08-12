import { render, screen } from '@testing-library/react';
import QuestionSetUpdatePage from '@/pages/questionSetUpdatePage/page';

describe('QuestionSetUpdatePage', () => {
  test('QuestionSetUpdatePage가 렌더링 된다.', () => {
    render(<QuestionSetUpdatePage />);
    expect(
      screen.getByRole('main', { name: 'question-set-update-page' }),
    ).toBeInTheDocument();
  });
});
