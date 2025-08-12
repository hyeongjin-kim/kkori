import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import MyQuestionSetListContainer from '@/pages/myQuestionSetPage/ui/MyQuestionSetListContainer';

describe('MyQuestionSetListContainer', () => {
  test('MyQuestionSetListContainer 컴포넌트가 렌더링 된다.', () => {
    render(<MemoryRouterWrapped component={<MyQuestionSetListContainer />} />);
    expect(
      screen.getByRole('list', { name: 'question-set-list' }),
    ).toBeInTheDocument();
  });
});
