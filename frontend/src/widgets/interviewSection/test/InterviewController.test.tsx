import { render, screen } from '@testing-library/react';
import InterviewController from '@/widgets/interviewSection/ui/InterviewController';
import useInterviewRoomStore, {
  interviewStatus,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';

describe('InterviewController', () => {
  const { setStatus } = useInterviewRoomStore.getState();

  beforeEach(() => {
    setStatus(interviewStatus.BEFORE_INTERVIEW);
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
    setStatus(interviewStatus.QUESTION_PRESENTED);
    render(<InterviewController />);
    const interviewController = screen.getByLabelText('interview-controller');
    expect(interviewController).toBeInTheDocument();
  });
});
