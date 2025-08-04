import { render, screen } from '@testing-library/react';
import PreInterviewControlButtonContainer from '@/components/practicePage/PreInterviewControlButtonContainer';

describe('PreInterviewControlButtonContainer', () => {
  beforeEach(() => {
    render(<PreInterviewControlButtonContainer />);
  });

  test('PreInterviewControlButtonContainer가 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('pre-interview-control-button-container'),
    ).toBeInTheDocument();
  });

  test('면접 시작 버튼이 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('interview-start-control-button'),
    ).toBeInTheDocument();
  });

  test('면접 종료 버튼이 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('interview-end-control-button'),
    ).toBeInTheDocument();
  });
});
