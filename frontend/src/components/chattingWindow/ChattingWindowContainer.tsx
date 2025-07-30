import ChattingInputBar from '@/components/chattingWindow/ChattingInputBar';
import ScrollableList from '@/components/chattingWindow/ScrollableList';

function ChattingWindowContainer() {
  return (
    <div
      role="log"
      aria-label="chatting-window-container"
      className="flex h-[95%] max-h-screen w-1/4 flex-col overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-md"
    >
      {/* 채팅 리스트 영역 */}
      <div className="flex-1 space-y-3 overflow-y-auto px-5 py-4">
        <ScrollableList />
      </div>

      <div className="border-t border-gray-200 bg-white px-4 py-3">
        <ChattingInputBar />
      </div>
    </div>
  );
}

export default ChattingWindowContainer;
