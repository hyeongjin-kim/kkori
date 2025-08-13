import AudioStateDisplay from '@/widgets/interviewSection/ui/AudioStateDisplay';
import { render, screen } from '@testing-library/react';

describe('AudioStateDisplay', () => {
  test('AudioStateDisplay가 렌더링 된다', () => {
    render(<AudioStateDisplay isAudioOn={false} onClick={() => {}} />);
    expect(screen.getByLabelText('audio-state-display')).toBeInTheDocument();
  });
});
