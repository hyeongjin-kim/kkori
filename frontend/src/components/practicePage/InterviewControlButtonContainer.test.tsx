import { render, screen } from '@testing-library/react';
import InterviewControlButtonContainer from '@/components/practicePage/InterviewControlButtonContainer';

describe('ControlButtonContainer', () => {
  beforeEach(() => {
    render(<InterviewControlButtonContainer />);
  });

  test('ControlButtonContainer가 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('control-button-container'),
    ).toBeInTheDocument();
  });

  test('화면 전환 버튼이 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('screen-change-control-button'),
    ).toBeInTheDocument();
  });

  test('답변 시작 버튼이 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('answer-start-control-button'),
    ).toBeInTheDocument();
  });

  test('답변 종료 버튼이 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('answer-end-control-button'),
    ).toBeInTheDocument();
  });
  test('면접 종료 버튼이 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('interview-end-control-button'),
    ).toBeInTheDocument();
  });
});
