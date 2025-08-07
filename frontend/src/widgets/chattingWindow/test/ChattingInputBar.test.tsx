import { render, screen } from '@testing-library/react';
jest.mock('@/widgets/chattingWindow/model/index');
import userEvent from '@testing-library/user-event';
import ChattingInputBar from '@/widgets/chattingWindow/ui/ChattingInputBar';
import { submitChatting } from '@/widgets/chattingWindow/model';
const mockSubmitChatting = submitChatting as jest.Mock;

describe('ChattingInputBar', () => {
  beforeEach(() => {
    render(<ChattingInputBar />);
  });

  test('채팅 입력창이 랜더링 되어야 한다.', () => {
    expect(
      screen.getByRole('textbox', { name: 'chatting-input' }),
    ).toBeInTheDocument();
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  test('엔터를 누르면 채팅을 제출하는 함수가 실행되어야 한다.', async () => {
    const textInput = screen.getByRole('textbox');
    await userEvent.type(textInput, 'test');
    await userEvent.keyboard('{Enter}');
    expect(mockSubmitChatting).toHaveBeenCalledWith(expect.any(Object));
  });
  test('버튼을 누르면 채팅을 제출하는 함수가 실행되어야 한다.', async () => {
    const sendButton = screen.getByRole('button');
    await userEvent.click(sendButton);
    expect(mockSubmitChatting).toHaveBeenCalledWith(expect.any(Object));
  });
});
