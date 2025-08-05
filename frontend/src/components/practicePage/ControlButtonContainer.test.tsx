import { render, screen } from '@testing-library/react';
import ControlButtonContainer from '@/components/practicePage/ControlButtonContainer';
import usePracticeStore from '@/stores/usePracticeStore';

describe('ControlButtonContainer', () => {
  const { setStatus } = usePracticeStore.getState();

  beforeEach(() => {
    setStatus('pre-interview');
  });

  test('렌더링 되어야 한다.', () => {
    render(<ControlButtonContainer />);
    const controlButtonContainer = screen.getByLabelText(
      'control-button-container',
    );
    expect(controlButtonContainer).toBeInTheDocument();
  });

  test('PreInterviewControlButtonContainer가 렌더링 되어야 한다.', () => {
    render(<ControlButtonContainer />);
    const preInterviewControlButtonContainer = screen.getByLabelText(
      'pre-interview-control-button-container',
    );
    expect(preInterviewControlButtonContainer).toBeInTheDocument();
  });

  test('InterviewControlButtonContainer가 렌더링 되어야 한다.', () => {
    setStatus('interview');
    render(<ControlButtonContainer />);
    const interviewControlButtonContainer = screen.getByLabelText(
      'interview-control-button-container',
    );
    expect(interviewControlButtonContainer).toBeInTheDocument();
  });
});
