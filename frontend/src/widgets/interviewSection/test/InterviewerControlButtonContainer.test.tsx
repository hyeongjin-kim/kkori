import { act, render, screen } from '@testing-library/react';
import useInterviewRoomStore, {
  interviewStatus,
  interviewType,
  interviewRole,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import InterviewerControlButtonContainer from '@/widgets/interviewSection/ui/InterviewerControlButtonContainer';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('InterviewerControlButtonContainer', () => {
  beforeEach(() => {
    render(
      <MemoryRouterWrapped component={<InterviewerControlButtonContainer />} />,
    );
  });
  test('InterviewerControlButtonContainer가 렌더링 되어야 한다.', () => {
    expect(
      screen.getByLabelText('interviewer-control-button-container'),
    ).toBeInTheDocument();
  });

  describe('Pair Mode', () => {
    beforeEach(() => {
      useInterviewRoomStore.getState().setType(interviewType.PAIR);
      useInterviewRoomStore
        .getState()
        .setStatus(interviewStatus.BEFORE_INTERVIEW);
      useInterviewRoomStore.getState().setRole(interviewRole.INTERVIEWER);
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
});
