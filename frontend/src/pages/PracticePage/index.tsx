import ChattingWindowContainer from '@/components/chattingWindow/ChattingWindowContainer';
import LeftSection from '@/components/practicePage/LeftSection';
import useInitMediaStream from '@/hooks/practicePage/useInitMediaStream';
import { useWebSocketStore } from '@/stores/useWebSocketStore';
import { useEffect } from 'react';

interface PracticePageProps {
  type: 'solo' | 'pair';
}

function PracticePage({ type }: PracticePageProps) {
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
      aria-label={`${type}-practice-page`}
      className="mt-16 flex h-full max-h-screen w-full items-center justify-center gap-5 px-8"
    >
      <LeftSection />
      <ChattingWindowContainer />
    </main>
  );
}

export default PracticePage;
