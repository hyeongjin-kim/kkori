import { render, screen } from '@testing-library/react';
import QuestionSetTagFilter from '@/pages/interviewQuestionsPage/ui/QuestionSetTagFilter';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('QuestionSetTagFilter', () => {
  test('QuestionSetTagFilter 컴포넌트가 렌더링되어야 합니다.', () => {
    render(<MemoryRouterWrapped component={<QuestionSetTagFilter />} />);
    expect(
      screen.getByRole('region', { name: 'question-set-tag-filter' }),
    ).toBeInTheDocument();
  });
});
