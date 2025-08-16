import { render, screen } from '@testing-library/react';
import NextQuestionModal from '@/widgets/interviewSection/ui/NextQuestionModal';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { createRef } from 'react';

describe('NextQuestionModal', () => {
  const onClose = jest.fn();
  const onCreate = jest.fn();
  const contentRef = createRef<HTMLDivElement>();
  beforeEach(() => {
    render(<NextQuestionModal onClose={onClose} contentRef={contentRef} />);
  });
  test('NextQuestionModal이 랜더링된다.', () => {
    expect(screen.getByLabelText('next-question-modal')).toBeInTheDocument();
  });
});
