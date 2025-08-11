import { render, screen } from '@testing-library/react';
import SectionCard from '@/shared/ui/SectionCard';

describe('SectionCard', () => {
  test('SectionCard 컴포넌트가 렌더링 된다.', () => {
    render(
      <SectionCard title="test" ariaLabel="test">
        <div>test</div>
      </SectionCard>,
    );
    expect(screen.getByRole('region', { name: 'test' })).toBeInTheDocument();
  });
});
