import { render, screen } from '@testing-library/react';
import LeftSection from '@/components/practicePage/LeftSection';
import { mockupQuestion } from '@/__mocks__/questionMocks';
describe('LeftSection', () => {
  beforeEach(() => {
    render(<LeftSection />);
  });

  test('LeftSection이 렌더링 되어야 한다.', () => {
    const leftSection = screen.getByLabelText('left-section');
    expect(leftSection).toBeInTheDocument();
  });

  test('현재 질문이 렌더링 되어야 한다.', () => {
    const currentQuestion = screen.getByLabelText('current-question-display');
    expect(currentQuestion).toBeInTheDocument();
  });

  test('메인 미디어 스트림이 렌더링 되어야 한다.', () => {
    const mainMediaStream = screen.getByLabelText('main-media-stream-viewer');
    expect(mainMediaStream).toBeInTheDocument();
  });

  test('PreInterviewControlButtonContainer가 렌더링 되어야 한다.', () => {
    const preInterviewControlButtonContainer = screen.getByLabelText(
      'pre-interview-control-button-container',
    );
    expect(preInterviewControlButtonContainer).toBeInTheDocument();
  });

  test('InterviewControlButtonContainer가 렌더링 되어야 한다.', () => {
    const interviewControlButtonContainer = screen.getByLabelText(
      'interview-control-button-container',
    );
    expect(interviewControlButtonContainer).toBeInTheDocument();
  });

  test('서브 미디어 스트림이 렌더링 되어야 한다.', () => {
    const subMediaStream = screen.getByLabelText('sub-media-stream-viewer');
    expect(subMediaStream).toBeInTheDocument();
  });
});
