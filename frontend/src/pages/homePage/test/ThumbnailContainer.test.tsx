import { render, screen } from '@testing-library/react';
import ThumbnailContainer from '@/pages/homePage/ui/ThumbnailContainer';

describe('ThumbnailContainer', () => {
  test('썸네일 컨테이너은 렌더링 되어야 한다', () => {
    render(<ThumbnailContainer />);
    expect(
      screen.getByRole('img', { name: 'thumbnail-container' }),
    ).toBeInTheDocument();
  });
});
