import AudioOffDisplay from '@/components/practicePage/AudioOffDisplay';
import { render, screen } from '@testing-library/react';

describe('AudioOffDisplay', () => {
  test('AudioOffDisplay가 렌더링 된다', () => {
    render(<AudioOffDisplay visible={true} />);
    expect(screen.getByLabelText('audio-off-display')).toBeInTheDocument();
  });

  test('AudioOffDisplay가 렌더링 되지 않는다', () => {
    render(<AudioOffDisplay visible={false} />);
    expect(screen.queryByLabelText('audio-off-display')).toBeNull();
  });
});
