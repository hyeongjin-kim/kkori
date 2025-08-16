import { render, screen } from '@testing-library/react';
import MyInterviewResultContainer from '@/pages/myInterviewResultPage/ui/MyInterviewResultContainer';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('MyInterviewResultContainer', () => {
  test('MyInterviewResultContainer 컴포넌트가 렌더링 된다.', () => {
    render(<MemoryRouterWrapped component={<MyInterviewResultContainer />} />);
    expect(
      screen.getByLabelText('my-interview-result-container'),
    ).toBeInTheDocument();
  });
});
