import { render, screen } from '@testing-library/react';
import NextQuestionModal from '@/widgets/interviewSection/ui/NextQuestionModal';

describe('NextQuestionModal', () => {
  beforeEach(() => {
    render(<NextQuestionModal />);
  });
  test('NextQuestionModal이 랜더링된다.', () => {
    expect(screen.getByLabelText('next-question-modal')).toBeInTheDocument();
  });

  test('꼬리 질문이 버튼이 랜더링된다.', () => {
    expect(screen.getByLabelText('tail-question')).toBeInTheDocument();
  });
  test('디폴트 질문이 버튼이 랜더링된다.', () => {
    expect(screen.getByLabelText('default-question')).toBeInTheDocument();
  });
  test('커스텀 질문이 버튼이 랜더링된다.', () => {
    expect(screen.getByLabelText('custom-question')).toBeInTheDocument();
  });
});
