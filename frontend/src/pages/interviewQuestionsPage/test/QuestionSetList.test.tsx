import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import QuestionSetList from '@/pages/interviewQuestionsPage/ui/QuestionSetList';

describe('QuestionSetList', () => {
  test('QuestionSetList 컴포넌트가 렌더링되어야 합니다.', () => {
    render(
      <MemoryRouter>
        <QuestionSetList />
      </MemoryRouter>,
    );
    screen.getByRole('list', { name: 'question-set-list' });
  });
});
