import { render, screen } from '@testing-library/react';
import MyInterviewResultPage from '@/pages/myInterviewResultPage/page/index';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('MyInterviewResultPage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<MyInterviewResultPage />} />);
  });

  test('MyInterviewResultPage 페이지가 렌더링 된다.', () => {
    expect(
      screen.getByRole('main', { name: 'my-interview-result-page' }),
    ).toBeInTheDocument();
  });
});
