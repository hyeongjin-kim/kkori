import { render, screen } from '@testing-library/react';
import TagFilter from '@/pages/interviewQuestionsPage/ui/TagFilter';
import userEvent from '@testing-library/user-event';
import { tagFilterStyle } from '@/pages/interviewQuestionsPage/ui/TagFilter';

describe('TagFilter', () => {
  const testFunction = jest.fn();
  beforeEach(() => {
    render(<TagFilter tag="interview" onClick={testFunction} />);
  });

  test('TagFilter 컴포넌트가 렌더링되어야 합니다.', () => {
    screen.getByRole('button', { name: 'tag-filter-interview' });
  });

  test('버튼을 클릭하면 태그가 선택된다.', async () => {
    const tagFilter = screen.getByRole('button', {
      name: 'tag-filter-interview',
    });
    await userEvent.click(tagFilter);
    expect(testFunction).toHaveBeenCalled();
  });
});

describe('tagFilterStyle', () => {
  [true, false].forEach(selected => {
    test(`${selected ? 'selected' : 'unselected'} 스타일이 적용되어야 한다.`, () => {
      render(<TagFilter tag="interview" selected={selected} />);
      const tagFilter = screen.getByRole('button', {
        name: 'tag-filter-interview',
      });
      expect(tagFilter).toHaveClass(
        selected ? tagFilterStyle.selected : tagFilterStyle.unselected,
      );
    });
  });
});
