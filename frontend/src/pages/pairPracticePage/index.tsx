import ChattingWindowContainer from '@/widgets/chattingWindow';
import InterviewSection from '@/widgets/interviewSection';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { useEffect } from 'react';
import { PRACTICE_MODE } from '@/pages/homePage/ui/PracticeButton';

function PairPracticePage() {
  const { error } = useInitMediaStream();
  const { connect, disconnect } = usePracticeSessionStore();

  useEffect(() => {
    //TODO: 1은 질문 넘버로 대체
    connect(PRACTICE_MODE.PAIR_PRACTICE, 1);

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
