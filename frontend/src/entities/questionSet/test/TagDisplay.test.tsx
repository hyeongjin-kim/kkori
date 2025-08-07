import { render, screen } from '@testing-library/react';
import TagDisplay from '@/entities/questionSet/ui/TagDisplay';
import userEvent from '@testing-library/user-event';

describe('TagDisplay', () => {
  const onClick = jest.fn();
  beforeEach(() => {
    const tags = ['tag1', 'tag2', 'tag3'];
    render(<TagDisplay tags={tags} onClick={onClick} />);
  });

  test('TagDisplay이 렌더링 된다.', () => {
    expect(screen.getByText('#tag1')).toBeInTheDocument();
    expect(screen.getByText('#tag2')).toBeInTheDocument();
    expect(screen.getByText('#tag3')).toBeInTheDocument();
  });

  test('TagDisplay의 태그를 클릭하면 onClick 함수가 호출된다.', async () => {
    await userEvent.click(screen.getByText('#tag1'));
    expect(onClick).toHaveBeenCalledWith('tag1');
  });
});
