import { act, render } from '@testing-library/react';
import InterviewRoleBadge from '@/widgets/interviewSection/ui/InterviewRoleBadge';
import useInterviewRoomStore, {
  interviewRole,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { screen } from '@testing-library/react';

describe('InterviewRoleBadge', () => {
  beforeEach(() => {
    render(<InterviewRoleBadge type="my" />);
  });
  test('면접관이면 면접관 배지가 렌더링 되어야 한다.', () => {
    act(() => {
      useInterviewRoomStore.getState().setRole(interviewRole.INTERVIEWER);
    });
    expect(screen.getByLabelText('interviewer-role-badge')).toBeInTheDocument();
  });
  test('면접자이면 면접자 배지가 렌더링 되어야 한다.', () => {
    act(() => {
      useInterviewRoomStore.getState().setRole(interviewRole.INTERVIEWEE);
    });
    expect(screen.getByLabelText('interviewee-role-badge')).toBeInTheDocument();
  });
});
