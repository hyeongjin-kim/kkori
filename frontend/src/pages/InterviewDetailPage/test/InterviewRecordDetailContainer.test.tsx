import { render, screen } from '@testing-library/react';
import InterviewRecordDetailContainer from '@/pages/InterviewDetailPage/ui/InterviewRecordDetailContainer';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('InterviewRecordDetailContainer', () => {
  test('InterviewRecordDetailContainer가 렌더링 되어야 한다.', () => {
    render(
      <MemoryRouterWrapped component={<InterviewRecordDetailContainer />} />,
    );
    expect(
      screen.getByLabelText('interview-record-detail-container'),
    ).toBeInTheDocument();
  });
});
