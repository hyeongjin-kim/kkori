import SoloPracticeButton from '@components/homePage/SoloPracticeButton';
import PairPracticeButton from '@components/homePage/PairPracticeButton';
import ThumbnailContainer from '@components/homePage/ThumbnailContainer';
import BackgroundShadow from '@components/homePage/BackgroundShadow';
import LeftSection from '@components/homePage/LeftSection';

function HomePage() {
  return (
    <main
      aria-label="home-page"
      className="bg-background relative flex h-full w-full items-center justify-around gap-4 overflow-hidden px-20 py-16"
    >
      <BackgroundShadow />
      <LeftSection />
      <ThumbnailContainer />
    </main>
  );
}

export default HomePage;
