import { render, screen } from '@testing-library/react';
import InterviewRecordEmptyState from '@/pages/myInterviewResultPage/ui/InterviewRecordEmptyState';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('InterviewRecordEmptyState', () => {
  test('InterviewRecordEmptyState 컴포넌트가 렌더링 된다.', () => {
    render(<MemoryRouterWrapped component={<InterviewRecordEmptyState />} />);
    expect(screen.getByLabelText('empty-interview-record')).toBeInTheDocument();
  });
});
