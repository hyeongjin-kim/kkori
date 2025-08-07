import {
  CHAT_TYPES,
  Message,
} from '@/widgets/chattingWindow/model/chattingWindowType';
import { QUESTION_ANSWER_MESSAGE_STYLE } from '@/widgets/chattingWindow/model/constants';
function QuestionAnswerMessage({ message }: { message: Message }) {
  const { sender, text, type } = message;
  const isQuestion = type === CHAT_TYPES.question;

  return (
    <div
      className={`flex flex-col ${isQuestion ? 'items-start' : 'items-end'} mb-3`}
    >
      <div className="mb-1 text-xs text-gray-400">{sender}</div>
      <div
        role="listitem"
        aria-label="question-answer-message"
        className={`max-w-3/4 rounded-2xl px-4 py-2 text-sm break-words whitespace-pre-wrap shadow-sm ${
          isQuestion
            ? QUESTION_ANSWER_MESSAGE_STYLE.question
            : QUESTION_ANSWER_MESSAGE_STYLE.answer
        }`}
      >
        {text}
      </div>
    </div>
  );
}

export default QuestionAnswerMessage;
