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
      <div className="w-full">
        <CurrentQuestionDisplay
          id={mockupQuestion.id}
          question={mockupQuestion.question}
        />
      </div>
      <div className="flex h-[75%] w-full gap-5">
        <div className="relative flex-[1.2] overflow-hidden rounded-xl bg-black shadow-md">
          <span className="absolute top-3 left-3 z-10 rounded-full bg-blue-500 px-3 py-1 text-xs font-medium text-white shadow">
            MAIN
          </span>
          <MainMediaStreamViewer />
        </div>

        <div className="relative flex-[0.8] overflow-hidden rounded-xl bg-black shadow-md">
          <span className="absolute top-3 left-3 z-10 rounded-full bg-blue-500 px-3 py-1 text-xs font-medium text-white shadow">
            SUB
          </span>
          <SubMediaStreamViewer />
        </div>
      </div>

      <div aria-label="bottom-section" className="flex w-full justify-end">
        <ControlButtonContainer />
      </div>
    </div>
  );
}

export default LeftSection;
