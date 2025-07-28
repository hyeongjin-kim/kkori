import { render, screen } from '@testing-library/react';
import NameTaggedMessage, {
  chatStyleMap,
} from '@/components/chattingWindow/NameTaggedMessage';
import { CHAT_TYPES } from '@/customTypes/practicePage/NameTaggedMessageProps';
import { mockMessageExamples } from '@/__mocks__/chatMocks';

describe('NameTaggedMessage', () => {
  test('이름 태그 메시지가 랜더링 되어야 한다.', () => {
    render(
      <NameTaggedMessage
        id="1"
        type={CHAT_TYPES.QUESTION}
        sender="tester"
        message="test"
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
          <NameTaggedMessage key={example.id} {...example} />
        ))}
      </>,
    );

    const items = screen.getAllByRole('listitem', {
      name: 'name-tagged-message',
    });

    items.forEach((item, idx) => {
      expect(item).toHaveClass(chatStyleMap[mockMessageExamples[idx].type]);
    });
  });
});
