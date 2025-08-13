import SoloPracticeButton from '@/pages/homePage/ui/SoloPracticeButton';
import PairPracticeButton from '@/pages/homePage/ui/PairPracticeButton';
import HeroText from '@/pages/homePage/ui/HeroText';
import { interviewRole } from '@/entities/interviewRoom/model/useInterviewRoomStore';

function LeftSection() {
  return (
    <section aria-label="left-section" className="flex flex-col gap-20">
      <HeroText />
      <div className="z-10 flex flex-col gap-8">
        <SoloPracticeButton />
        <PairPracticeButton role={interviewRole.INTERVIEWER} />
        <PairPracticeButton role={interviewRole.INTERVIEWEE} />
      </div>
    </section>
  );
}

export default LeftSection;
