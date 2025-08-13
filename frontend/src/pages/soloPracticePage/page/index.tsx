import ChattingWindowContainer from '@/widgets/chattingWindow';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { useEffect } from 'react';
import InterviewSection from '@/widgets/interviewSection';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';
import { PRACTICE_MODE } from '@/pages/homePage/ui/PracticeButton';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import NextQuestionModal from '@/widgets/interviewSection/ui/NextQuestionModal';

function SoloPracticePage() {
  const { error } = useInitMediaStream();
  const { connect, disconnect } = usePracticeSessionStore();

  useEffect(() => {
    useInterviewRoomStore.getState().setStatus('beforeInterview');
    useInterviewRoomStore.getState().setRole('interviewee');
    useInterviewRoomStore.getState().setInterviewType('solo');
    connect();
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
