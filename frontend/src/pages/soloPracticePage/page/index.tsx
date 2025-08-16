import ChattingWindowContainer from '@/widgets/chattingWindow';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { useEffect } from 'react';
import InterviewSection from '@/widgets/interviewSection';
import useInterviewRoomStore, {
  interviewStatus,
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import NextQuestionModal from '@/widgets/interviewSection/ui/NextQuestionModal';

function SoloPracticePage() {
  const { connect, disconnect } = usePracticeSessionStore();

  useEffect(() => {
    connect();
    useInterviewRoomStore
      .getState()
      .setStatus(interviewStatus.BEFORE_INTERVIEW);
    return () => {
      disconnect();
    };
  }, []);

  return (
    <main
      aria-label={`solo-practice-page`}
      className="flex h-full max-h-screen w-full items-center justify-center gap-5 px-8"
    >
      <NextQuestionModal />
      <InterviewSection />
      <ChattingWindowContainer />
    </main>
  );
}

export default SoloPracticePage;
