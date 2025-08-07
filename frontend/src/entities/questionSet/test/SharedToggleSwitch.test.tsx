import { render, screen } from '@testing-library/react';
import SharedToggleSwitch from '@/entities/questionSet/ui/SharedToggleSwitch';

describe('SharedToggleSwitch', () => {
  test('SharedToggleSwitch 렌더링 된다.', () => {
    render(
      <SharedToggleSwitch
        displayTitle="shared-toggle-switch"
        value={false}
        onChange={() => {}}
      />,
    );
    expect(screen.getByLabelText('shared-toggle-switch')).toBeInTheDocument();
  });
});
