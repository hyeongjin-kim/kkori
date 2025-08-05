import ChattingInputBar from '@/widgets/chattingWindow/ui/ChattingInputBar';
import ScrollableList from '@/widgets/chattingWindow/ui/ScrollableList';

function ChattingWindowContainer() {
  return (
    <div
      role="log"
      aria-label="chatting-window-container"
      className="flex h-[calc(100vh-8rem)] max-h-[calc(100vh-8rem)] w-1/4 flex-col overflow-hidden rounded-2xl border border-gray-200 bg-white py-8 shadow-md"
    >
      <ScrollableList />
      <ChattingInputBar />
    </div>
  );
}

export default ChattingWindowContainer;
