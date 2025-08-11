import { render, screen } from '@testing-library/react';
import TagListInput from '@/entities/questionSet/ui/TagListInput';

describe('TagListInput', () => {
  test('TagListInput이 렌더링 된다.', () => {
    render(
      <TagListInput
        displayTitle="tag-list-input"
        value=""
        onChange={() => {}}
        onSubmit={() => {}}
      />,
    );
    expect(
      screen.getByRole('textbox', { name: 'tag-input' }),
    ).toBeInTheDocument();
  });
});
