import SubmitChatting from '@/components/chattingWindow/SubmitChatting';

function ChattingInputBar() {
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    SubmitChatting(e);
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="flex items-center justify-between gap-3 py-3"
    >
      {/* 채팅 입력창 (너비 조절) */}
      <input
        type="text"
        name="text"
        aria-label="chatting-input"
        placeholder="메시지를 입력하세요"
        autoComplete="off"
        className="h-10 w-[80%] rounded-full bg-gray-50 px-4 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:bg-white focus:ring-2 focus:ring-blue-200 focus:outline-none"
      />

      {/* 버튼 (고정 너비) */}
      <button
        type="submit"
        className="flex h-10 w-[80px] items-center justify-center rounded-full bg-blue-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-600 active:scale-95"
      >
        보내기
      </button>
    </form>
  );
}

export default ChattingInputBar;
