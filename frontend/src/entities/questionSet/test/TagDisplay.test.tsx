import { render, screen } from '@testing-library/react';
import TagDisplay from '@/entities/questionSet/ui/TagDisplay';

describe('TagDisplay', () => {
  test('TagDisplay이 렌더링 된다.', () => {
    render(<TagDisplay tags={['tag1', 'tag2', 'tag3']} />);
    expect(screen.getByText('#tag1')).toBeInTheDocument();
    expect(screen.getByText('#tag2')).toBeInTheDocument();
    expect(screen.getByText('#tag3')).toBeInTheDocument();
  });
});
