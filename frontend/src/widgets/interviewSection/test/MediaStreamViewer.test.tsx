import { render, screen } from '@testing-library/react';
import MediaStreamViewer from '@/widgets/interviewSection/ui/MediaStreamViewer';
import useMediaStreamStore from '@/stores/useMediaStreamStore';

describe('MediaStreamViewer 스트림 없을 경우', () => {
  beforeEach(() => {
    useMediaStreamStore.setState({
      myStream: null,
      isMyVideoOn: false,
      isMyAudioOn: false,
    });
    render(<MediaStreamViewer type="my" />);
  });

  test('MediaStreamViewer가 렌더링 된다', () => {
    expect(screen.getByLabelText('my-media-stream-viewer')).toBeInTheDocument();
  });

  test('VideoStream의 opacity가 0이다', () => {
    const video = screen.getByLabelText('video-stream');
    expect(video).toHaveClass('opacity-0');
  });

  test('VideoPlaceholder가 렌더링된다', () => {
    expect(screen.getByLabelText('video-placeholder')).toBeInTheDocument();
  });

  test('AudioOffDisplay가 렌더링된다', () => {
    expect(screen.getByLabelText('audio-off-display')).toBeInTheDocument();
  });
});

describe('MediaStreamViewer 스트림 있을 경우', () => {
  beforeEach(() => {
    useMediaStreamStore.setState({
      myStream: new MediaStream(),
      isMyVideoOn: true,
      isMyAudioOn: true,
    });
    render(<MediaStreamViewer type="my" />);
  });

  test('MediaStreamViewer가 렌더링 된다', () => {
    expect(screen.getByLabelText('my-media-stream-viewer')).toBeInTheDocument();
  });

  test('VideoStream의 opacity가 1이다', () => {
    const video = screen.getByLabelText('video-stream');
    expect(video).not.toHaveClass('opacity-0');
  });

  test('VideoPlaceholder가 렌더링되지 않는다', () => {
    expect(screen.queryByLabelText('video-placeholder')).toBeNull();
  });

  test('AudioOffDisplay가 렌더링되지 않는다', () => {
    expect(screen.queryByLabelText('audio-off-display')).toBeNull();
  });
});
