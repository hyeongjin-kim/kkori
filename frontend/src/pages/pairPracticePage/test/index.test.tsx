import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import PairPracticePage from '@/pages/pairPracticePage/page/index';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';

describe('PairPracticePage', () => {
  class MockMediaStream {
    id = 'mock';
    active = true;
    getTracks() {
      return [];
    }
    getAudioTracks() {
      return [];
    }
    getVideoTracks() {
      return [];
    }
    addTrack() {}
    removeTrack() {}
  }
  (global as any).MediaStream = MockMediaStream as any;

  const initSpy = jest.spyOn(useMediaStreamStore.getState(), 'initMyStream');

  beforeEach(() => {
    if (!useMediaStreamStore.getState().initMyStream) {
      useMediaStreamStore.setState({ initMyStream: () => {} } as any, true);
    }
    initSpy.mockImplementation(async () => {
      return new Promise(resolve => {
        setTimeout(() => {
          resolve(new MockMediaStream() as any);
        }, 0);
      });
    });
    useMediaStreamStore.setState({
      myStream: new MockMediaStream() as any,
    } as any);
  });

  afterEach(() => {
    initSpy.mockRestore();
    useMediaStreamStore.setState({ myStream: null } as any);
    jest.clearAllMocks();
  });

  test('PairPracticePage 페이지가 렌더링 된다.', () => {
    render(<MemoryRouterWrapped component={<PairPracticePage />} />);
    expect(
      screen.getByRole('main', { name: 'pair-practice-page' }),
    ).toBeInTheDocument();
    expect(initSpy).toHaveBeenCalled();
  });
});
