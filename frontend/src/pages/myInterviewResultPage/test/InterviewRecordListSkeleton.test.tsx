import { render, screen } from '@testing-library/react';
import InterviewRecordListSkeleton from '@/pages/myInterviewResultPage/ui/InterviewRecordListSkeleton';

describe('InterviewRecordListSkeleton', () => {
  test('InterviewRecordListSkeleton 컴포넌트가 렌더링 된다.', () => {
    render(<InterviewRecordListSkeleton />);
    expect(
      screen.getByLabelText('interview-record-list-skeleton'),
    ).toBeInTheDocument();
  });
});
