import { render, screen } from '@testing-library/react';
import TagFilter from '@/pages/interviewQuestionsPage/ui/TagFilter';

describe('TagFilter', () => {
  test('TagFilter 컴포넌트가 렌더링되어야 합니다.', () => {
    render(<TagFilter tag={{ id: 1, tag: 'interview' }} />);
    screen.getByRole('button', { name: 'tag-filter-interview' });
  });
});
