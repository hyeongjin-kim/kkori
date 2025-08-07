import { render, screen } from '@testing-library/react';
import QuestionSetForm from '@/pages/questionSetCreatePage/ui/QuestionSetForm';

describe('QuestionSetForm', () => {
  test('QuestionSetForm이 렌더링 된다.', () => {
    render(<QuestionSetForm />);
    expect(
      screen.getByRole('region', { name: 'question-set-form' }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole('heading', { name: '질문 세트 생성' }),
    ).toBeInTheDocument();
    expect(screen.getByLabelText('title-input')).toBeInTheDocument();
    expect(screen.getByLabelText('description-input')).toBeInTheDocument();
    expect(screen.getByLabelText('shared-toggle-switch')).toBeInTheDocument();
    expect(screen.getByLabelText('tag-input')).toBeInTheDocument();
  });
});
