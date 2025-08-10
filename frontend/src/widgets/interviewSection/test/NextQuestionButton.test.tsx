import { render, screen } from '@testing-library/react';
import NextQuestionButton from '@/widgets/interviewSection/ui/NextQuestionButton';

describe('NextQuestionButton', () => {
  beforeEach(() => {
    render(
      <NextQuestionButton
        nextQuestion="tail-question"
        label="tail-question"
        onClick={() => {}}
      />,
    );
  });

  test('NextQuestionButton이 랜더링된다.', () => {
    expect(screen.getByLabelText('tail-question')).toBeInTheDocument();
  });
});
