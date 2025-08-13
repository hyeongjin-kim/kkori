import SoloPracticeButton from '@/pages/homePage/ui/SoloPracticeButton';
import PairPracticeButton from '@/pages/homePage/ui/PairPracticeButton';
import HeroText from '@/pages/homePage/ui/HeroText';

interface LeftSectionProps {
  onQuestionSetModalOpen: () => void;
  onJoinInterviewRoomModalOpen: () => void;
}

function LeftSection({
  onQuestionSetModalOpen,
  onJoinInterviewRoomModalOpen,
}: LeftSectionProps) {
  return (
    <section aria-label="left-section" className="flex flex-col gap-20">
      <HeroText />
      <div className="z-10 flex flex-col gap-8">
        <SoloPracticeButton onClick={onQuestionSetModalOpen} />
        <PairPracticeButton onClick={onJoinInterviewRoomModalOpen} />
      </div>
    </section>
  );
}

export default LeftSection;
