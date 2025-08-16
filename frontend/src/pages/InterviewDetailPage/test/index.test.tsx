import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import InterviewDetailPage from '@/pages/InterviewDetailPage/page';

describe('InterviewDetailPage', () => {
  test('InterviewDetailPage가 렌더링 되어야 한다.', () => {
    render(<MemoryRouterWrapped component={<InterviewDetailPage />} />);
    expect(
      screen.getByLabelText('interview-record-detail-page'),
    ).toBeInTheDocument();
  });
});
