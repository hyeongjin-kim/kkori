import { render, screen } from '@testing-library/react';
import QuestionAnswerForm from '@/pages/questionSetCreatePage/ui/QuestionAnswerForm';

describe('QuestionAnswerForm', () => {
  test('QuestionAnswerForm의 값이 변경되면 onChange 함수가 호출된다.', async () => {
    render(<QuestionAnswerForm />);
    expect(screen.getByLabelText('question-answer-form')).toBeInTheDocument();
  });
});
