import { render, screen } from '@testing-library/react';
import ControlButton from '@/widgets/interviewSection/ui/ControlButton';
import userEvent from '@testing-library/user-event';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';

describe('ControlButton', () => {
  const testFunction = jest.fn();

  beforeEach(() => {
    useInterviewRoomStore.setState({
      status: 'BEFORE_INTERVIEW',
    });
    render(
      <ControlButton
        onClick={testFunction}
        label="test"
        text="test"
        status="BEFORE_INTERVIEW"
      />,
    );
  });

  test('컨트롤 버튼이 렌더링 된다.', () => {
    expect(screen.getByLabelText('test-control-button')).toBeInTheDocument();
  });
  test('컨트롤 버튼이 클릭되면 onClick 함수가 호출된다.', async () => {
    const testButton = screen.getByLabelText('test-control-button');
    await userEvent.click(testButton);
    expect(testFunction).toHaveBeenCalled();
  });
});
