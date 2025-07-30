import ChattingWindowContainer from '@/components/chattingWindow/ChattingWindowContainer';
import LeftSection from '@/components/practicePage/LeftSection';
import useInitMediaStream from '@/hooks/practicePage/useInitMediaStream';

function PracticePage() {
  const { error } = useInitMediaStream();

  return (
    <main
      aria-label="practice-page"
      className="mt-16 flex h-full max-h-screen w-full items-center justify-center gap-5 px-8"
    >
      <LeftSection />
      <ChattingWindowContainer />
    </main>
  );
}

export default PracticePage;
