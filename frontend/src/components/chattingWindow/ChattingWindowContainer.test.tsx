import { render, screen } from '@testing-library/react';
import ChattingWindowContainer from '@/components/chattingWindow/ChattingWindowContainer';

describe('ChattingWindowContainer', () => {
  beforeEach(() => {
    render(<ChattingWindowContainer />);
  });

  test('채팅 창이 랜더링 되어야 한다.', () => {
    expect(
      screen.getByRole('list', { name: 'scrollable-list' }),
    ).toBeInTheDocument();
  });

  test('채팅 창에 채팅 입력창이 랜더링 되어야 한다.', () => {
    expect(
      screen.getByRole('textbox', { name: 'chatting-input' }),
    ).toBeInTheDocument();
  });
});
