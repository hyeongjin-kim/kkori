import { render, screen } from '@testing-library/react';
import { QueryBoundary } from '@/shared/ui/QueryBoundary';

describe('QueryBoundary', () => {
  test('QueryBoundary 컴포넌트가 렌더링 된다.', () => {
    render(
      <QueryBoundary
        pendingFallback={<div>Loading...</div>}
        errorFallback={() => <div>Error</div>}
      >
        <div>test</div>
      </QueryBoundary>,
    );
    expect(screen.getByText('test')).toBeInTheDocument();
  });
});
