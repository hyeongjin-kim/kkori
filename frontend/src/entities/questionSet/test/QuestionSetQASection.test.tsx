import { render, screen } from '@testing-library/react';
import QuestionSetQASection from '@/entities/questionSet/ui/QuestionSetQASection';

describe('QuestionSetQASection', () => {
  test('QuestionSetQASection 컴포넌트가 렌더링 된다.', () => {
    render(<QuestionSetQASection />);
    expect(screen.getByLabelText('qa-with-tails')).toBeInTheDocument();
  });
});
