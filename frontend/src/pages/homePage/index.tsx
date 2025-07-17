import SoloPracticeButton from "../../components/homePage/SoloPracticeButton";
import PairPracticeButton from "../../components/homePage/PairPracticeButton";

function HomePage() {
  return (
    <div className="flex h-screen w-full flex-col items-center justify-center gap-4 bg-[var(--color-background)] px-4">
      <h1 className="mb-6 text-2xl font-bold text-[var(--color-text-white)]">
        HomePage
      </h1>
      <SoloPracticeButton />
      <PairPracticeButton />
    </div>
  );
}

export default HomePage;
