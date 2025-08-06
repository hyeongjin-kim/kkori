import ChattingInputBar from '@/widgets/chattingWindow/ui/ChattingInputBar';
import ScrollableList from '@/shared/ui/ScrollableList';
import NameTaggedMessage from '@/widgets/chattingWindow/ui/NameTaggedChatMessage';
import useChattingWindowStore from '@/stores/useChattingWindowStore';
import { CHAT_TYPES } from '@/widgets/chattingWindow/model/chattingWindowType';
import QuestionAnswerMessage from '@/widgets/chattingWindow/ui/QuestionAnswerMessage';

function ChattingWindowContainer() {
  const messages = useChattingWindowStore(state => state.messages);

  return (
    <div
      role="log"
      aria-label="chatting-window-container"
      className="flex h-[calc(100vh-8rem)] max-h-[calc(100vh-8rem)] w-1/4 flex-col overflow-hidden rounded-2xl border border-gray-200 bg-white py-8 shadow-md"
    >
      <ScrollableList>
        {messages.map(message =>
          message.type === CHAT_TYPES.CHAT ? (
            <NameTaggedMessage key={message.id} message={message} />
          ) : (
            <QuestionAnswerMessage key={message.id} message={message} />
          ),
        )}
      </ScrollableList>
      <ChattingInputBar />
    </div>
  );
}

export default ChattingWindowContainer;
