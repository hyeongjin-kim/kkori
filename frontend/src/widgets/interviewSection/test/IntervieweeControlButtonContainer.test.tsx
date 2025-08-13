import { render, screen } from '@testing-library/react';
import IntervieweeControlButtonContainer from '@/widgets/interviewSection/ui/IntervieweeControlButtonContainer';
import useInterviewRoomStore, {
  interviewStatus,
  interviewType,
  interviewRole,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { act } from 'react';

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
      useInterviewRoomStore.getState().setType(interviewType.SOLO);
      useInterviewRoomStore
        .getState()
        .setStatus(interviewStatus.BEFORE_INTERVIEW);
      useInterviewRoomStore.getState().setRole(interviewRole.INTERVIEWEE);
    });
    test('화면 전환 버튼은 항상 렌더링 되어야 한다.', () => {
      expect(
        screen.getByLabelText('screen-change-control-button'),
      ).toBeInTheDocument();
    });
    test('면접 시작 전에는 면접 시작 버튼이 렌더링 되어야 한다.', () => {
      useInterviewRoomStore
        .getState()
        .setStatus(interviewStatus.BEFORE_INTERVIEW);
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
      act(() => {
        useInterviewRoomStore
          .getState()
          .setStatus(interviewStatus.QUESTION_PRESENTED);
      });
      expect(
        screen.getByLabelText('answer-start-control-button'),
      ).toBeInTheDocument();
    });
    test('답변을 시작하면 답변 종료 버튼이 렌더링 되어야 한다.', () => {
      act(() => {
        useInterviewRoomStore
          .getState()
          .setStatus(interviewStatus.ANSWER_START);
      });
      expect(
        screen.getByLabelText('answer-end-control-button'),
      ).toBeInTheDocument();
    });
    test('답변이 종료되면 다음 질문 선택 버튼이 렌더링 되어야 한다.', () => {
      act(() => {
        useInterviewRoomStore
          .getState()
          .setStatus(interviewStatus.NEXT_QUESTION_PRESENTED);
      });
      expect(
        screen.getByLabelText('next-question-select-control-button'),
      ).toBeInTheDocument();
    });
  });
  describe('Pair Mode', () => {
    beforeEach(() => {
      useInterviewRoomStore.getState().setType(interviewType.PAIR);
      useInterviewRoomStore
        .getState()
        .setStatus(interviewStatus.BEFORE_INTERVIEW);
      useInterviewRoomStore.getState().setRole(interviewRole.INTERVIEWEE);
    });
    test('화면 전환 버튼은 항상 렌더링 되어야 한다.', () => {
      expect(
        screen.getByLabelText('screen-change-control-button'),
      ).toBeInTheDocument();
    });
    test('면접 종료 버튼은 항상 렌더링 되어야 한다.', () => {
      expect(
        screen.getByLabelText('interview-end-control-button'),
      ).toBeInTheDocument();
    });
    test('면접 시작 전에는 면접 시작 버튼이 렌더링 되어야 한다.', () => {
      useInterviewRoomStore
        .getState()
        .setStatus(interviewStatus.BEFORE_INTERVIEW);
      expect(
        screen.getByLabelText('interview-start-control-button'),
      ).toBeInTheDocument();
    });
    test('면접이 시작되면 답변하기 버튼이 렌더링 되어야 한다.', () => {
      act(() => {
        useInterviewRoomStore
          .getState()
          .setStatus(interviewStatus.QUESTION_PRESENTED);
      });
      expect(
        screen.getByLabelText('answer-start-control-button'),
      ).toBeInTheDocument();
    });
    test('답변을 시작하면 답변 종료 버튼이 렌더링 되어야 한다.', () => {
      act(() => {
        useInterviewRoomStore
          .getState()
          .setStatus(interviewStatus.ANSWER_START);
      });
      expect(
        screen.getByLabelText('answer-end-control-button'),
      ).toBeInTheDocument();
    });
  });
});
