import NameTaggedMessage from '@/components/chattingWindow/NameTaggedMessage';
import { NameTaggedMessageProps } from '@/customTypes/practicePage/NameTaggedMessageProps';
import useChattingWindowStore from '@/stores/useChattingWindowStore';

function ScrollableList() {
  const messages = useChattingWindowStore(state => state.messages);
  return (
    <div role="list" aria-label="scrollable-list">
      <ul>
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
    </div>
  );
}

export default ScrollableList;
