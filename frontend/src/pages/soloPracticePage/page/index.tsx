import ChattingWindowContainer from '@/widgets/chattingWindow';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { useEffect } from 'react';
import InterviewSection from '@/widgets/interviewSection';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';
import useInterviewRoomStore, {
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import NextQuestionModal from '@/widgets/interviewSection/ui/NextQuestionModal';

function SoloPracticePage() {
  const { error } = useInitMediaStream();
  const { connect, disconnect } = usePracticeSessionStore();

  useEffect(() => {
    //TODO: 1은 질문 넘버로 대체
    useInterviewRoomStore.getState().setStatus('beforeInterview');
    useInterviewRoomStore.getState().setType(interviewType.SOLO);
    connect(1);
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
