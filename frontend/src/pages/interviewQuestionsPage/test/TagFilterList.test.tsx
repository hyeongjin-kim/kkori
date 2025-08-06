import { render, screen } from '@testing-library/react';
import TagFilterList from '@/pages/interviewQuestionsPage/ui/TagFilterList';

describe('TagFilterList', () => {
  test('TagFilterList 컴포넌트가 렌더링되어야 합니다.', () => {
    render(<TagFilterList selectedTag={null} onClick={() => {}} />);
    screen.getByRole('list', { name: 'tag-filter-list' });
  });
});
