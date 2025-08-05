import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import InterviewQuestionsPage from '@/pages/interviewQuestionsPage/page/index';

describe('InterviewQuestionsPage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<InterviewQuestionsPage />} />);
  });

  test('InterviewQuestionsPage 페이지가 렌더링 된다.', () => {
    expect(
      screen.getByRole('main', { name: 'interview-questions-page' }),
    ).toBeInTheDocument();
  });
});
