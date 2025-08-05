import {
  CHAT_TYPES,
  NameTaggedMessageProps,
} from '@/widgets/chattingWindow/model/NameTaggedMessageProps';

export const chatStyleMap: Record<
  (typeof CHAT_TYPES)[keyof typeof CHAT_TYPES],
  string
> = {
  [CHAT_TYPES.QUESTION]: 'bg-gray-100 text-gray-800',
  [CHAT_TYPES.ANSWER]: 'bg-yellow-100 text-gray-800',
  [CHAT_TYPES.OPPONENT]: 'bg-blue-100 text-gray-800',
  [CHAT_TYPES.USER]: 'bg-green-100 text-gray-800',
};

function NameTaggedMessage({ sender, message, type }: NameTaggedMessageProps) {
  const isRightAligned = type === CHAT_TYPES.ANSWER || type === CHAT_TYPES.USER;

  return (
    <div
      className={`flex flex-col ${isRightAligned ? 'items-end' : 'items-start'} mb-3`}
    >
      <div className="mb-1 text-xs text-gray-400">{sender}</div>
      <div
        role="listitem"
        aria-label="name-tagged-message"
        className={`max-w-[75%] rounded-2xl px-4 py-2 text-sm break-words whitespace-pre-wrap shadow-sm ${chatStyleMap[type]} ${
          isRightAligned
            ? 'rounded-tl-2xl rounded-tr-md rounded-bl-2xl'
            : 'rounded-tl-md rounded-tr-2xl rounded-br-2xl'
        }`}
      >
        {message}
      </div>
    </div>
  );
}

export default NameTaggedMessage;
