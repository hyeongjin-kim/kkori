import ThumbnailContainer from '@/pages/homePage/ui/ThumbnailContainer';
import BackgroundShadow from '@/pages/homePage/ui/BackgroundShadow';
import LeftSection from '@/pages/homePage/ui/LeftSection';
import { useModal } from '@/shared/lib/useModal';
import SelectQuestionSetModal from '@/pages/homePage/ui/SelectQuestionSetModal';
import JoinInterviewRoomModal from '@/pages/homePage/ui/JoinInterviewRoomModal';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';

function HomePage() {
  const questionSetModal = useModal();
  const joinRoomModal = useModal();
  const handleJoinClose = () => {
    joinRoomModal.close();
  };

  const handleCreateRoom = () => {
    joinRoomModal.close();
    questionSetModal.open();
  };
  return (
    <main
      aria-label="home-page"
      className="bg-background relative flex h-full max-h-screen w-full items-center justify-around gap-4 overflow-hidden px-20 py-16"
    >
      <BackgroundShadow />
      <LeftSection
        onQuestionSetModalOpen={questionSetModal.open}
        onJoinInterviewRoomModalOpen={joinRoomModal.open}
      />
      <ThumbnailContainer />

      {questionSetModal.isOpen && (
        <SelectQuestionSetModal
          onClose={questionSetModal.close}
          contentRef={questionSetModal.contentRef}
        />
      )}

      {joinRoomModal.isOpen && (
        <JoinInterviewRoomModal
          onClose={handleJoinClose}
          onCreate={handleCreateRoom}
          contentRef={joinRoomModal.contentRef}
        />
      )}
    </main>
  );
}

export default HomePage;
