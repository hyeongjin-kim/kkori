import { render, screen } from '@testing-library/react';
import VideoPlaceholder from '@/widgets/interviewSection/ui/VideoPlaceholder';

describe('VideoPlaceholder', () => {
  test('VideoPlaceholder가 렌더링 된다', () => {
    render(<VideoPlaceholder visible={true} />);
    expect(screen.getByLabelText('video-placeholder')).toBeInTheDocument();
  });
});
