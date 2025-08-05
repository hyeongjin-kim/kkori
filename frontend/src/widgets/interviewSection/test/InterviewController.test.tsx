import { render, screen } from '@testing-library/react';
import InterviewController from '@/widgets/interviewSection/ui/InterviewController';
import usePracticeStore from '@/stores/usePracticeStore';

describe('InterviewController', () => {
  const { setStatus } = usePracticeStore.getState();

  beforeEach(() => {
    setStatus('pre-interview');
  });

  test('InterviewController가 렌더링 되어야 한다.', () => {
    render(<InterviewController />);
    const interviewController = screen.getByLabelText('interview-controller');
    expect(interviewController).toBeInTheDocument();
  });

  test('PreInterviewControlButtonContainer가 렌더링 되어야 한다.', () => {
    render(<InterviewController />);
    const preInterviewControlButtonContainer = screen.getByLabelText(
      'pre-interview-control-button-container',
    );
    expect(preInterviewControlButtonContainer).toBeInTheDocument();
  });

  test('InterviewControlButtonContainer가 렌더링 되어야 한다.', () => {
    setStatus('interview');
    render(<InterviewController />);
    const interviewController = screen.getByLabelText('interview-controller');
    expect(interviewController).toBeInTheDocument();
  });
});
