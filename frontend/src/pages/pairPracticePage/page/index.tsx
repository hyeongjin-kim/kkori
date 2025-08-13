import ChattingWindowContainer from '@/widgets/chattingWindow';
import InterviewSection from '@/widgets/interviewSection';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { useEffect } from 'react';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
function PairPracticePage() {
  const { error } = useInitMediaStream();
  const { connect, disconnect } = usePracticeSessionStore();

  useEffect(() => {
    useInterviewRoomStore.getState().setInterviewType('pair');
    useInterviewRoomStore.getState().setStatus('beforeInterview');
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
