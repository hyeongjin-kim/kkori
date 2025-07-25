import SoloPracticeButton from '@components/homePage/SoloPracticeButton';
import PairPracticeButton from '@components/homePage/PairPracticeButton';

function HomePage() {
  return (
    <main
      aria-label="home-page"
      className="flex h-screen w-full flex-col items-center justify-center gap-4 bg-[var(--color-background)] px-4"
    >
      <SoloPracticeButton />
      <PairPracticeButton />
    </main>
  );
}

export default HomePage;
