import { render, screen } from '@testing-library/react';
import MediaStreamViewer from '@/components/practicePage/MediaStreamViewer';
import useMediaStreamStore from '@/stores/useMediaStreamStore';

describe('MediaStreamViewer', () => {
  beforeEach(() => {
    useMediaStreamStore.setState({
      myStream: null,
      peerStream: null,
      isMyVideoOn: false,
      isMyAudioOn: false,
      isPeerVideoOn: false,
      isPeerAudioOn: false,
    });
    render(<MediaStreamViewer type="my" />);
  });

  test('MediaStreamViewer가 렌더링 된다', () => {
    expect(screen.getByLabelText('media-stream-viewer')).toBeInTheDocument();
  });

  test('MediaStream이 없을 경우 빈 화면이 렌더링 된다', () => {
    expect(
      screen.getByRole('img', { name: 'empty-video-stream' }),
    ).toBeInTheDocument();
  });

  test('MediaStream이 있을 경우 빈 화면이 렌더링 되지 않는다', () => {
    expect(
      screen.getByRole('video', { name: 'video-stream' }),
    ).toBeInTheDocument();
  });

  test('사용자가 비디오 스트림을 껐을 때 비디오 꺼짐 이미지가 렌더링 된다', () => {
    const isVideoStreamOn = useMediaStreamStore(state => state.isMyVideoOn);
    expect(isVideoStreamOn).toBe(false);
    expect(
      screen.getByRole('img', { name: 'video-off-image' }),
    ).toBeInTheDocument();
  });
});
