import { render, screen } from '@testing-library/react';
import LeftSection from '@/pages/homePage/ui/LeftSection';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('LeftSection', () => {
  test('LeftSection이 렌더링 된다', () => {
    render(<MemoryRouterWrapped component={<LeftSection />} />);
    expect(screen.getByLabelText('left-section')).toBeInTheDocument();
  });
});
