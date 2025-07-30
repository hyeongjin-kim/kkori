import { render, screen } from '@testing-library/react';
import ControlButton from '@/components/practicePage/ControlButton';
import userEvent from '@testing-library/user-event';

describe('ControlButton', () => {
  const testFunction = jest.fn();

  beforeEach(() => {
    render(<ControlButton onClick={testFunction} label="test" text="test" />);
  });

  test('화면 전환 버튼이 렌더링 된다.', () => {
    expect(screen.getByLabelText('test-control-button')).toBeInTheDocument();
  });
  test('화면 전환 버튼이 클릭되면 switchScreen 함수가 호출된다.', async () => {
    const testButton = screen.getByLabelText('test-control-button');
    await userEvent.click(testButton);
    expect(testFunction).toHaveBeenCalled();
  });
});
