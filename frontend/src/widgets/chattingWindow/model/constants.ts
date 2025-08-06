const MESSAGE_ALIGN_STYLE = {
  left: 'rounded-tl-md rounded-tr-2xl rounded-br-2xl',
  right: 'rounded-tl-2xl rounded-tr-md rounded-bl-2xl',
} as const;

export const CHAT_MESSAGE_STYLE = {
  me: `bg-green-100 text-gray-800 ${MESSAGE_ALIGN_STYLE.right}`,
  opponent: `bg-blue-100 text-gray-800 ${MESSAGE_ALIGN_STYLE.left}`,
} as const;

export const QUESTION_ANSWER_MESSAGE_STYLE = {
  question: `bg-gray-100 text-gray-800 ${MESSAGE_ALIGN_STYLE.left}`,
  answer: `bg-yellow-100 text-gray-800 ${MESSAGE_ALIGN_STYLE.right}`,
} as const;
