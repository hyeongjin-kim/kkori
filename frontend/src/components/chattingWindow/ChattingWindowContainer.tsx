import ChattingInputBar from '@/components/chattingWindow/ChattingInputBar';
import ScrollableList from '@/components/chattingWindow/ScrollableList';

function ChattingWindowContainer() {
  return (
    <div role="log" aria-label="chatting-window-container">
      <ScrollableList />
      <ChattingInputBar />
    </div>
  );
}

export default ChattingWindowContainer;
