import { render, screen } from '@testing-library/react';
import NameTaggedMessage from '@/widgets/chattingWindow/ui/NameTaggedChatMessage';
import { CHAT_TYPES } from '@/widgets/chattingWindow/model/chattingWindowType';
import { mockMessageExamples } from '@/__mocks__/chatMocks';
import QuestionAnswerMessage from '@/widgets/chattingWindow/ui/QuestionAnswerMessage';

describe('QuestionAnswerMessage', () => {
  [CHAT_TYPES.question, CHAT_TYPES.answer].forEach(type => {
    test(`${type} 메시지가 랜더링 되어야 한다.`, () => {
      render(
        <QuestionAnswerMessage
          message={{
            id: '1',
            type,
            sender: 'tester',
            text: 'test',
            timestamp: new Date().toISOString(),
            isMyMessage: false,
          }}
        />,
      );

      expect(
        screen.getByRole('listitem', { name: 'question-answer-message' }),
      ).toBeInTheDocument();
      expect(screen.getByText('test')).toBeInTheDocument();
      expect(screen.getByText('tester')).toBeInTheDocument();
    });
  });

  test('질문 메시지의 타입에 따라 메시지가 다른 스타일로 렌더링 되어야 한다.', () => {
    render(
      <>
        {mockMessageExamples.map(example => (
          <QuestionAnswerMessage key={example.id} message={example} />
        ))}
      </>,
    );

    const items = screen.getAllByRole('listitem', {
      name: 'question-answer-message',
    });

    items.forEach((item, idx) => {
      expect(item).toHaveClass(
        mockMessageExamples[idx].type === CHAT_TYPES.question
          ? 'bg-gray-100 text-gray-800'
          : 'bg-yellow-100 text-gray-800',
      );
    });
  });
});
