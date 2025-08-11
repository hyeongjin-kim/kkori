import { render, screen } from '@testing-library/react';
import QuestionSet from '@/pages/interviewQuestionsPage/ui/QuestionSet';
import { questionSetList } from '@/entities/questionSet/model/mock';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('QuestionSet', () => {
  test('QuestionSet 컴포넌트가 렌더링되어야 합니다.', () => {
    render(
      <MemoryRouterWrapped
        component={<QuestionSet questionSet={questionSetList[0]} />}
      />,
    );
    expect(screen.getByLabelText('question-set')).toBeInTheDocument();
  });
});
