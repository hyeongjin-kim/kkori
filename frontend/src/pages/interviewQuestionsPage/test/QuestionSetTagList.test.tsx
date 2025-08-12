import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import { render, screen } from '@testing-library/react';
import QuestionSet from '@/pages/interviewQuestionsPage/ui/QuestionSet';
import { questionSetList } from '@/entities/questionSet/model/mock';

describe('QuestionSetTagList', () => {
  beforeEach(() => {
    render(
      <MemoryRouterWrapped
        component={<QuestionSet questionSet={questionSetList[0]} />}
      />,
    );
  });
  test('QuestionSetTagList가 렌더링 된다.', () => {
    expect(
      screen.getByRole('list', { name: 'question-set-tag-list' }),
    ).toBeInTheDocument();
  });
});
