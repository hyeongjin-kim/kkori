import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import MyQuestionSetPage from '@/pages/myQuestionSetPage/page/index';

describe('MyQuestionSetPage', () => {
  test('MyQuestionSetPage 페이지가 렌더링 된다.', () => {
    render(<MemoryRouterWrapped component={<MyQuestionSetPage />} />);
    expect(
      screen.getByRole('main', { name: 'my-question-set-page' }),
    ).toBeInTheDocument();
  });
});
