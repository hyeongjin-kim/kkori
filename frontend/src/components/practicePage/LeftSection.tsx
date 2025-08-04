import CurrentQuestionDisplay from '@/components/practicePage/CurrentQuestionDisplay';
import MainMediaStreamViewer from '@/components/practicePage/MainMediaStreamViewer';
import SubMediaStreamViewer from '@/components/practicePage/SubMediaStreamViewer';
import { mockupQuestion } from '@/__mocks__/questionMocks';
import ControlButtonContainer from './ControlButtonContainer';
function LeftSection() {
  return (
    <div
      aria-label="left-section"
      className="flex h-[95%] max-h-screen w-3/4 flex-col items-center justify-between gap-6 rounded-2xl border border-gray-200 bg-white px-8 py-10 shadow-md"
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
