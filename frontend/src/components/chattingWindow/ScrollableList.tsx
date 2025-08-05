import NameTaggedMessage from '@/components/chattingWindow/NameTaggedMessage';
import { NameTaggedMessageProps } from '@/customTypes/practicePage/NameTaggedMessageProps';
import useChattingWindowStore from '@/stores/useChattingWindowStore';
import { useLayoutEffect } from 'react';

const useScrollToBottom = () => {
  const messages = useChattingWindowStore(state => state.messages);
  useLayoutEffect(() => {
    const list = document.querySelector('[aria-label="scrollable-list"]');
    list?.scrollTo({ top: list.scrollHeight, behavior: 'smooth' });
  }, [messages]);
  return messages;
};

function ScrollableList() {
  const messages = useScrollToBottom();

  return (
    <ul
      className="h-full w-full overflow-y-auto px-5"
      aria-label="scrollable-list"
    >
      {messages.map(message => (
        <NameTaggedMessage
          key={message.type + message.id}
          id={message.id}
          type={message.type}
          message={message.message}
          sender={message.sender}
        />
      ))}
    </ul>
  );
}

export default ScrollableList;
