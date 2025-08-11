import { render, screen } from '@testing-library/react';
import QuestionSet from '@/pages/interviewQuestionsPage/ui/QuestionSet';
import { questionSetList } from '@/entities/questionSet/model/mock';
import { MemoryRouter } from 'react-router-dom';

describe('QuestionSet', () => {
  test('QuestionSet 컴포넌트가 렌더링되어야 합니다.', () => {
    render(
      <MemoryRouter>
        <QuestionSet questionSet={questionSetList[0]} />
      </MemoryRouter>,
    );
    screen.getByRole('listitem', { name: 'question-set' });
  });
});
