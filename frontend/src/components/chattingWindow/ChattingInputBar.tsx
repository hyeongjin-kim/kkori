import SubmitChatting from '@/components/chattingWindow/SubmitChatting';

function ChattingInputBar() {
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    SubmitChatting(e);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="text" name="text" aria-label="chatting-input" />
      <button type="submit">Send</button>
    </form>
  );
}

export default ChattingInputBar;
