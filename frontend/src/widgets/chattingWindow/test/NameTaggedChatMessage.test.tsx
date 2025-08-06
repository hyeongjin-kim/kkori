import { render, screen } from '@testing-library/react';
import NameTaggedMessage from '@/widgets/chattingWindow/ui/NameTaggedChatMessage';
import { CHAT_TYPES } from '@/widgets/chattingWindow/model/chattingWindowType';
import { mockMessageExamples } from '@/__mocks__/chatMocks';
import { CHAT_MESSAGE_STYLE } from '@/widgets/chattingWindow/model/constants';

describe('NameTaggedMessage', () => {
  test('이름 태그 메시지가 랜더링 되어야 한다.', () => {
    render(
      <NameTaggedMessage
        message={{
          id: '1',
          type: CHAT_TYPES.QUESTION,
          sender: 'tester',
          text: 'test',
          timestamp: new Date().toISOString(),
          isMyMessage: false,
        }}
      />,
    );

    expect(
      screen.getByRole('listitem', { name: 'name-tagged-message' }),
    ).toBeInTheDocument();
    expect(screen.getByText('test')).toBeInTheDocument();
    expect(screen.getByText('tester')).toBeInTheDocument();
  });
  test('이름 태그 메시지의 타입에 따라 메시지가 다른 스타일로 렌더링 되어야 한다.', () => {
    render(
      <>
        {mockMessageExamples.map(example => (
          <NameTaggedMessage key={example.id} message={example} />
        ))}
      </>,
    );

    const items = screen.getAllByRole('listitem', {
      name: 'name-tagged-message',
    });

    items.forEach((item, idx) => {
      expect(item).toHaveClass(
        mockMessageExamples[idx].isMyMessage
          ? CHAT_MESSAGE_STYLE.me
          : CHAT_MESSAGE_STYLE.opponent,
      );
    });
  });
});
