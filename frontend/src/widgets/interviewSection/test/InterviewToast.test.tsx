import { render, screen, waitFor, cleanup } from '@testing-library/react';
import InterviewToast from '@/widgets/interviewSection/ui/InterviewToast';
import useInterviewToastStore from '../model/useInterviewToastStore';
import { act } from 'react';

describe('InterviewToast', () => {
  const DURATION = 3000;
  const EXIT_ANIM = 200;

  beforeAll(() => {
    jest.useFakeTimers();
  });

  afterAll(() => {
    jest.useRealTimers();
  });

  beforeEach(() => {
    useInterviewToastStore.setState({ toasts: [] });
  });

  afterEach(() => {
    cleanup();
  });

  test('InterviewToast 컴포넌트가 렌더링 된다.', () => {
    render(<InterviewToast />);
    expect(screen.getByLabelText('interview-toast')).toBeInTheDocument();
  });

  test('토스트를 추가하면 토스트가 렌더링된다.', async () => {
    render(<InterviewToast />);
    const addToast = useInterviewToastStore.getState().addToast;

    act(() => {
      addToast('test');
    });

    expect(await screen.findByText('test')).toBeInTheDocument();
  });

  test('토스트가 일정 시간이 지나면 사라진다.', async () => {
    render(<InterviewToast />);
    const addToast = useInterviewToastStore.getState().addToast;
    const MSG = '토스트가 일정 시간이 지나면 사라진다.';

    act(() => {
      addToast(MSG);
    });

    expect(await screen.findByText(MSG)).toBeInTheDocument();

    act(() => {
      jest.advanceTimersByTime(DURATION + EXIT_ANIM);
    });

    await waitFor(() => {
      expect(screen.queryByText(MSG)).toBeNull();
    });
  });
});
