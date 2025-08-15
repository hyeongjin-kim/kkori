import ChattingWindowContainer from '@/widgets/chattingWindow';
import InterviewSection from '@/widgets/interviewSection';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { useEffect } from 'react';
import useInterviewRoomStore, {
  interviewStatus,
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import NextQuestionModal from '@/widgets/interviewSection/ui/NextQuestionModal';
import { useModal } from '@/shared/lib/useModal';
function PairPracticePage() {
  const { connect, disconnect } = usePracticeSessionStore();

  useEffect(() => {
    useInterviewRoomStore.getState().setType(interviewType.PAIR);
    useInterviewRoomStore
      .getState()
      .setStatus(interviewStatus.BEFORE_INTERVIEW);
    connect();
    return () => {
      disconnect();
    };
  }, []);
  const nextQuestionModal = useModal();
  return (
    <main
      aria-label={`pair-practice-page`}
      className="flex h-full max-h-screen w-full items-center justify-center gap-5 px-8"
    >
      <NextQuestionModal
        onClose={nextQuestionModal.close}
        contentRef={nextQuestionModal.contentRef}
      />
      <InterviewSection openModal={nextQuestionModal.open} />
      <ChattingWindowContainer />
    </main>
  );
}

export default PairPracticePage;
