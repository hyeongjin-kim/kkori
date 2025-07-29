import {
  CHAT_TYPES,
  NameTaggedMessageProps,
} from '@/customTypes/practicePage/NameTaggedMessageProps';

export const chatStyleMap: Record<
  (typeof CHAT_TYPES)[keyof typeof CHAT_TYPES],
  string
> = {
  [CHAT_TYPES.QUESTION]: 'bg-gray-200 text-black rounded-tl-lg rounded-br-lg',
  [CHAT_TYPES.ANSWER]: 'bg-yellow-100 text-black rounded-tr-lg rounded-bl-lg',
  [CHAT_TYPES.OPPONENT]: 'bg-blue-200 text-black rounded-bl-lg rounded-tr-lg',
  [CHAT_TYPES.USER]: 'bg-green-100 text-black rounded-br-lg rounded-tl-lg',
};

function NameTaggedMessage({ sender, message, type }: NameTaggedMessageProps) {
  return (
    <div
      className={
        type === CHAT_TYPES.ANSWER || type === CHAT_TYPES.USER
          ? 'flex justify-end'
          : 'flex justify-start'
      }
    >
      <div className="absolute top-0 left-0">{sender}</div>
      <div
        role="listitem"
        aria-label="name-tagged-message"
        className={`inline-block max-w-[70%] rounded-xl px-4 py-2 break-words ${chatStyleMap[type]}`}
      >
        {message}
      </div>
    </div>
  );
}

export default NameTaggedMessage;
