import { render, screen } from '@testing-library/react';
import IntervieweeControlButtonContainer from '@/widgets/interviewSection/ui/IntervieweeControlButtonContainer';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';

describe('IntervieweeControlButtonContainer', () => {
  beforeEach(() => {
    render(<IntervieweeControlButtonContainer />);
  });
  test('IntervieweeControlButtonContainer가 렌더링 되어야 한다.', () => {
    expect(
      screen.getByLabelText('interviewee-control-button-container'),
    ).toBeInTheDocument();
  });

  describe('Sole Mode', () => {
    beforeEach(() => {
      useInterviewRoomStore.setState({
        interviewType: 'solo',
      });
    });
    test('면접 시작 전에는 면접 시작 버튼이 렌더링 되어야 한다.', () => {
      useInterviewRoomStore.setState({
        status: 'BEFORE_INTERVIEW',
      });
      expect(
        screen.getByLabelText('interview-start-control-button'),
      ).toBeInTheDocument();
    });
    test('면접 종료 버튼은 항상 렌더링 되어야 한다.', () => {
      expect(
        screen.getByLabelText('interview-end-control-button'),
      ).toBeInTheDocument();
    });
    test('면접이 시작되면 답변하기 버튼이 렌더링 되어야 한다.', () => {
      useInterviewRoomStore.setState({
        status: 'QUESTION_PRESENTED',
      });
      expect(
        screen.getByLabelText('answer-start-control-button'),
      ).toBeInTheDocument();
    });
    test('답변을 시작하면 답변 종료 버튼이 렌더링 되어야 한다.', () => {
      useInterviewRoomStore.setState({
        status: 'ANSWER_START',
      });
      expect(
        screen.getByLabelText('interview-end-control-button'),
      ).toBeInTheDocument();
    });
    test('답변이 종료되면 다음 질문 선택 버튼이 렌더링 되어야 한다.', () => {
      useInterviewRoomStore.setState({
        status: 'ANSWER_END',
      });
      expect(
        screen.getByLabelText('next-question-select-control-button'),
      ).toBeInTheDocument();
    });
  });
});
