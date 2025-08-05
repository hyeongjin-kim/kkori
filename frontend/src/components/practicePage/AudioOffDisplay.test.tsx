import AudioOffDisplay from '@/components/practicePage/AudioOffDisplay';
import { render, screen } from '@testing-library/react';

describe('AudioOffDisplay', () => {
  test('AudioOffDisplay가 렌더링 된다', () => {
    render(<AudioOffDisplay />);
    expect(screen.getByLabelText('audio-off-display')).toBeInTheDocument();
  });
});
