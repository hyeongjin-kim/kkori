import { render, screen } from '@testing-library/react';
import ScrollableList from '@/widgets/chattingWindow/ui/ScrollableList';
import { mockMessageExamples } from '@/__mocks__/chatMocks';
import useChattingWindowStore from '@/stores/useChattingWindowStore';

describe('ScrollableList', () => {
  beforeEach(() => {
    useChattingWindowStore.setState({ messages: mockMessageExamples });
    render(<ScrollableList />);
  });

  test('스크롤 가능한 리스트가 랜더링 되어야 한다.', () => {
    expect(
      screen.getByRole('list', { name: 'scrollable-list' }),
    ).toBeInTheDocument();
  });

  test('스크롤 가능한 리스트에 메시지가 랜더링 되어야 한다.', () => {
    mockMessageExamples.forEach(example => {
      expect(screen.getByText(example.message)).toBeInTheDocument();
      expect(screen.getByText(example.sender)).toBeInTheDocument();
    });
  });
});
