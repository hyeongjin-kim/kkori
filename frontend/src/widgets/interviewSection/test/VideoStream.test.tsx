import { render, screen } from '@testing-library/react';
import VideoStream from '@/widgets/interviewSection/ui/VideoStream';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';

describe('VideoStream', () => {
  beforeEach(() => {
    useMediaStreamStore.getState().reset();
    render(<VideoStream type="my" />);
  });

  test('VideoStream이 렌더링 된다', () => {
    expect(screen.getByLabelText('video-stream')).toBeInTheDocument();
  });

  test('VideoStream이 있을 경우 비디오 스트림이 렌더링 된다', () => {
    expect(screen.getByLabelText('video-stream')).toBeInTheDocument();
  });
});
