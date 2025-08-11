import { render, screen } from '@testing-library/react';
import QuestionSetList from '@/pages/interviewQuestionsPage/ui/QuestionSetList';

describe('QuestionSetList', () => {
  test('QuestionSetList 컴포넌트가 렌더링되어야 합니다.', () => {
    render(<QuestionSetList />);
    screen.getByRole('list', { name: 'question-set-list' });
  });
});
