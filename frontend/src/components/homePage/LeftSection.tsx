import SoloPracticeButton from '@components/homePage/SoloPracticeButton';
import PairPracticeButton from '@components/homePage/PairPracticeButton';
import HeroText from '@components/homePage/HeroText';

function LeftSection() {
  return (
    <section aria-label="left-section" className="flex flex-col gap-20">
      <HeroText />
      <div className="z-10 flex flex-col gap-8">
        <SoloPracticeButton />
        <PairPracticeButton />
      </div>
    </section>
  );
}

export default LeftSection;
