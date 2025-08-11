import { render, screen } from '@testing-library/react';
import QuestionSetOverviewCard from '@/entities/questionSet/ui/QuestionSetOverviewCard';
import { questionSetList } from '@/entities/questionSet/model/mock';
import { MemoryRouter } from 'react-router-dom';
import { toOverviewVM } from '@/entities/questionSet/model/toOverviewVM';

describe('QuestionSetOverviewCard', () => {
  test('QuestionSetOverviewCard 컴포넌트가 렌더링 된다.', () => {
    render(
      <MemoryRouter>
        <QuestionSetOverviewCard vm={toOverviewVM(questionSetList[0])} />
      </MemoryRouter>,
    );
    screen.getByRole('region', { name: 'question-set-overview' });
  });
});
