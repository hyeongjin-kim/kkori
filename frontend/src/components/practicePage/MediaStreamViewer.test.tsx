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

  test('사용자가 비디오 스트림을 껐을 때 비디오 꺼짐 이미지가 렌더링 된다', () => {
    const isVideoStreamOn = useMediaStreamStore(state => state.isMyVideoOn);
    expect(isVideoStreamOn).toBe(false);
    expect(
      screen.getByRole('img', { name: 'video-off-image' }),
    ).toBeInTheDocument();
  });

  test('사용자가 오디오 스트림을 껐을 때 오디오 꺼짐 이미지가 렌더링 된다', () => {
    const isAudioStreamOn = useMediaStreamStore(state => state.isMyAudioOn);
    expect(isAudioStreamOn).toBe(false);
    expect(
      screen.getByRole('img', { name: 'audio-off-image' }),
    ).toBeInTheDocument();
  });
});
