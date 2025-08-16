import { render, screen } from '@testing-library/react';
import InterviewRecordList from '@/pages/myInterviewResultPage/ui/InterviewRecordList';

describe('InterviewRecordList', () => {
  test('InterviewRecordList 컴포넌트가 렌더링 된다.', () => {
    render(
      <InterviewRecordList
        interviewRecords={[]}
        isLoading={false}
        onClick={() => {}}
      />,
    );
    expect(screen.getByLabelText('interview-record-list')).toBeInTheDocument();
  });
});
