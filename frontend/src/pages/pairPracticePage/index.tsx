import ChattingWindowContainer from '@/widgets/chattingWindow';
import InterviewSection from '@/widgets/interviewSection';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';
import { useWebSocketStore } from '@/shared/lib/useWebSocketStore';
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
      <InterviewSection />
      <ChattingWindowContainer />
    </main>
  );
}

export default PairPracticePage;
