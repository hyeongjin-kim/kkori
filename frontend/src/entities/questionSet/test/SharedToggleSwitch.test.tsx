import { render, screen } from '@testing-library/react';
import SharedToggleSwitch from '@/entities/questionSet/ui/SharedToggleSwitch';

describe('SharedToggleSwitch', () => {
  test('SharedToggleSwitch 렌더링 된다.', () => {
    render(
      <SharedToggleSwitch
        value={false}
        onChange={() => {}}
        displayTitle="shared-toggle-switch"
      />,
    );
    expect(
      screen.getByRole('checkbox', { name: 'toggle-shared-checkbox' }),
    ).toBeInTheDocument();
  });
});
