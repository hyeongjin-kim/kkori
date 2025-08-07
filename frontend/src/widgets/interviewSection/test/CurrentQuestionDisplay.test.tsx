import { render, screen } from '@testing-library/react';
import CurrentQuestionDisplay from '@/widgets/interviewSection/ui/CurrentQuestionDisplay';

describe('CurrentQuestionDisplay', () => {
  beforeEach(() => {
    render(<CurrentQuestionDisplay />);
  });

  test('CurrentQuestionDisplay 컴포넌트가 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('current-question-display'),
    ).toBeInTheDocument();
  });
});
