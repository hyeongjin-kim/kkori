import ChattingWindowContainer from '@/widgets/chattingWindow';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { useEffect } from 'react';
import InterviewSection from '@/widgets/interviewSection';
import useInterviewRoomStore, {
  interviewStatus,
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import NextQuestionModal from '@/widgets/interviewSection/ui/NextQuestionModal';
import { useModal } from '@/shared/lib/useModal';
import { useInterviewQuestionStore } from '@/widgets/interviewSection/model/useInterviewQuestionStore';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';

function SoloPracticePage() {
  const { connect, disconnect } = usePracticeSessionStore();
  const nextQuestionModal = useModal();
  useMediaStreamStore.getState().initMyStream();
  const handleNextQuestionModalClose = () => {
    nextQuestionModal.close();
  };
  useEffect(() => {
    connect();
    useInterviewRoomStore
      .getState()
      .setStatus(interviewStatus.BEFORE_INTERVIEW);
    return () => {
      disconnect();
      useInterviewQuestionStore.getState().clearCurrentQuestion();
      nextQuestionModal.close();
    };
  }, []);

  return (
    <main
      aria-label={`solo-practice-page`}
      className="flex h-full max-h-screen w-full items-center justify-center gap-5 px-8"
    >
      {nextQuestionModal.isOpen && (
        <NextQuestionModal
          onClose={handleNextQuestionModalClose}
          contentRef={nextQuestionModal.contentRef}
        />
      )}
      <InterviewSection openModal={nextQuestionModal.open} />
      <ChattingWindowContainer />
    </main>
  );
}

export default SoloPracticePage;
