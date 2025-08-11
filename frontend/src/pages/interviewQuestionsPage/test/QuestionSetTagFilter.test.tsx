import { render, screen } from '@testing-library/react';
import QuestionSetTagFilter from '@/pages/interviewQuestionsPage/ui/QuestionSetTagFilter';

describe('QuestionSetTagFilter', () => {
  test('QuestionSetTagFilter 컴포넌트가 렌더링되어야 합니다.', () => {
    render(<QuestionSetTagFilter />);
    screen.getByRole('region', { name: 'question-set-tag-filter' });
  });
});
