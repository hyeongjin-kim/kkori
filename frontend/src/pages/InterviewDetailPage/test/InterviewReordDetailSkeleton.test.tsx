import { render, screen } from '@testing-library/react';
import InterviewRecordDetailSkeleton from '@/pages/InterviewDetailPage/ui/InterviewReordDetailSkeleton';

describe('InterviewRecordDetailSkeleton', () => {
  test('InterviewRecordDetailSkeleton가 렌더링 되어야 한다.', () => {
    render(<InterviewRecordDetailSkeleton />);
    expect(
      screen.getByLabelText('interview-record-detail-skeleton'),
    ).toBeInTheDocument();
  });
});
