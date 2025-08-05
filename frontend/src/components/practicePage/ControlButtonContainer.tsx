import usePracticeStore from '@/stores/usePracticeStore';
import PreInterviewControlButtonContainer from '@/components/practicePage/PreInterviewControlButtonContainer';
import InterviewControlButtonContainer from '@/components/practicePage/InterviewControlButtonContainer';
import { PracticeType } from '@/stores/usePracticeStore';

const switchStatus = (status: PracticeType) => {
  switch (status) {
    case 'pre-interview':
      return <PreInterviewControlButtonContainer />;
    case 'interview':
      return <InterviewControlButtonContainer />;
  }
};

function ControlButtonContainer() {
  const status = usePracticeStore(state => state.status);

  return (
    <div aria-label="control-button-container">{switchStatus(status)}</div>
  );
}

export default ControlButtonContainer;
