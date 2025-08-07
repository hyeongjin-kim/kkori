import { render, screen } from '@testing-library/react';
import SharedToggleSwitch from '@/entities/questionSet/ui/SharedToggleSwitch';
import userEvent from '@testing-library/user-event';

describe('SharedToggleSwitch', () => {
  const onChange = jest.fn();
  beforeEach(() => {
    jest.clearAllMocks();
    render(
      <SharedToggleSwitch
        displayTitle="shared-toggle-switch"
        value={false}
        onChange={onChange}
      />,
    );
  });

  test('SharedToggleSwitch 렌더링 된다.', () => {
    expect(screen.getByLabelText('shared-toggle-switch')).toBeInTheDocument();
  });

  test('SharedToggleSwitch의 값이 변경되면 onChange 함수가 호출된다.', async () => {
    await userEvent.click(screen.getByLabelText('shared-toggle'));
    expect(onChange).toHaveBeenCalledWith(true);
  });
});
