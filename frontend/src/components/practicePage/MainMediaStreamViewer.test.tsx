import { render, screen } from '@testing-library/react';
import MainMediaStreamViewer from '@/components/practicePage/MainMediaStreamViewer';

describe('MainMediaStreamViewer', () => {
  test(`MainMediaStreamViewer가 렌더링 된다`, () => {
    render(<MainMediaStreamViewer />);
    expect(
      screen.getByLabelText('main-media-stream-viewer'),
    ).toBeInTheDocument();
  });
});
