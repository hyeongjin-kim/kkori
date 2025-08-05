import CurrentQuestionDisplay from '@/components/practicePage/CurrentQuestionDisplay';
import MainMediaStreamViewer from '@/components/practicePage/MainMediaStreamViewer';
import SubMediaStreamViewer from '@/components/practicePage/SubMediaStreamViewer';
import ControlButtonContainer from '@/components/practicePage/ControlButtonContainer';

function LeftSection() {
  return (
    <div
      aria-label="left-section"
      className="flex h-[calc(100vh-8rem)] max-h-[calc(100vh-8rem)] w-3/4 flex-col items-center justify-between rounded-2xl border border-gray-200 bg-white px-8 py-8 shadow-md"
    >
      <CurrentQuestionDisplay />
      <div className="flex h-[75%] w-full gap-5">
        <MainMediaStreamViewer />
        <SubMediaStreamViewer />
      </div>
      <ControlButtonContainer />
    </div>
  );
}

export default LeftSection;
