import { render, screen } from '@testing-library/react';
import QuestionSetListPage from '@/pages/questionSetListPage/page';

describe('questionSetListPage', () => {
  test('질문 세트 조회 페이지가 렌더링 된다.', () => {
    render(<QuestionSetListPage />);

    expect(
      screen.getByRole('main', { name: 'question-set-list-page' }),
    ).toBeInTheDocument();
  });
});
