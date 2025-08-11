import { render, screen } from '@testing-library/react';
import QuestionSetSkeleton from '@/pages/interviewQuestionsPage/ui/QuestionSetListSkeleton';

describe('QuestionSetSkeleton', () => {
  test('QuestionSetSkeleton이 렌더링 되어야 한다.', () => {
    render(<QuestionSetSkeleton />);
    expect(
      screen.getByRole('listitem', { name: 'question-set-skeleton' }),
    ).toBeInTheDocument();
  });
});
