import ChattingWindowContainer from '@/widgets/chattingWindow';
import LeftSection from '@/widgets/interviewController/ui/LeftSection';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';
import { useWebSocketStore } from '@/stores/useWebSocketStore';
import { useEffect } from 'react';

function PairPracticePage() {
  const { error } = useInitMediaStream();
  const { connect, disconnect } = useWebSocketStore();

  useEffect(() => {
    connect();

    return () => {
      disconnect();
    };
  }, []);

  return (
    <main
      aria-label={`pair-practice-page`}
      className="flex h-full max-h-screen w-full items-center justify-center gap-5 px-8"
    >
      <LeftSection />
      <ChattingWindowContainer />
    </main>
  );
}

export default PairPracticePage;
