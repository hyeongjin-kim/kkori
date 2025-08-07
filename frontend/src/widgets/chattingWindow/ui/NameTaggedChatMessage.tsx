import { NameTaggedChatMessageProps } from '@/widgets/chattingWindow/model/chattingWindowType';
import { CHAT_MESSAGE_STYLE } from '@/widgets/chattingWindow/model/constants';

function NameTaggedChatMessage({ message }: NameTaggedChatMessageProps) {
  const { sender, text, isMyMessage = false } = message;

  return (
    <div
      className={`flex flex-col ${isMyMessage ? 'items-end' : 'items-start'} mb-3`}
    >
      <div className="mb-1 text-xs text-gray-400">{sender}</div>
      <div
        role="listitem"
        aria-label="name-tagged-message"
        className={`max-w-3/4 rounded-2xl px-4 py-2 text-sm break-words whitespace-pre-wrap shadow-sm ${
          isMyMessage ? CHAT_MESSAGE_STYLE.me : CHAT_MESSAGE_STYLE.opponent
        }`}
      >
        {text}
      </div>
    </div>
  );
}

export default NameTaggedChatMessage;
