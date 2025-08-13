import SoloPracticeButton from '@/pages/homePage/ui/SoloPracticeButton';
import PairPracticeButton from '@/pages/homePage/ui/PairPracticeButton';
import ThumbnailContainer from '@/pages/homePage/ui/ThumbnailContainer';
import BackgroundShadow from '@/pages/homePage/ui/BackgroundShadow';
import LeftSection from '@/pages/homePage/ui/LeftSection';
import useInitMediaStream from '@/widgets/interviewSection/model/useInitMediaStream';

function HomePage() {
  const { error } = useInitMediaStream();
  return (
    <main
      aria-label="home-page"
      className="bg-background relative flex h-full max-h-screen w-full items-center justify-around gap-4 overflow-hidden px-20 py-16"
    >
      <BackgroundShadow />
      <LeftSection />
      <ThumbnailContainer />
    </main>
  );
}

export default HomePage;
