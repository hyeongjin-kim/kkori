import { render, screen } from '@testing-library/react';
import QuestionSet from '@/pages/interviewQuestionsPage/ui/QuestionSet';
import { questionSetList } from '@/entities/questionSet/model/mock';

describe('QuestionSet', () => {
  test('QuestionSet 컴포넌트가 렌더링되어야 합니다.', () => {
    render(<QuestionSet questionSet={questionSetList[0]} />);
    screen.getByRole('listitem', { name: 'question-set' });
  });
});
