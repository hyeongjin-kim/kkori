import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';
import PracticePage from '@/pages/practicePage/index';

describe('PracticePage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<PracticePage type="solo" />} />);
  });

  test('PracticePage 페이지가 렌더링 된다.', () => {
    expect(
      screen.getByRole('main', { name: 'solo-practice-page' }),
    ).toBeInTheDocument();
  });
  test('LeftSection이 렌더링 되어야 한다.', () => {
    const leftSection = screen.getByLabelText('left-section');
    expect(leftSection).toBeInTheDocument();
  });
  test('ChattingWindowContainer가 렌더링 되어야 한다.', () => {
    const chattingWindowContainer = screen.getByLabelText(
      'chatting-window-container',
    );
    expect(chattingWindowContainer).toBeInTheDocument();
  });
});
