import { render, screen } from '@testing-library/react';
import SubMediaStreamViewer from '@/components/practicePage/SubMediaStreamViewer';
import { MediaStreamType } from '@customTypes/practicePage/MediaStreamType';

describe('SubMediaStreamViewer', () => {
  test(`SubMediaStreamViewer가 렌더링 된다`, () => {
    render(<SubMediaStreamViewer />);
    expect(
      screen.getByLabelText('sub-media-stream-viewer'),
    ).toBeInTheDocument();
  });
});
