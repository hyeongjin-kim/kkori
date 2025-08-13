import VideoStateDisplay from '@/widgets/interviewSection/ui/VideoStateDisplay';
import { render, screen } from '@testing-library/react';

describe('VideoStateDisplay', () => {
  test('VideoStateDisplay가 렌더링 된다', () => {
    render(<VideoStateDisplay isVideoOn={false} onClick={() => {}} />);
    expect(screen.getByLabelText('video-state-display')).toBeInTheDocument();
  });
});
