import { render, screen } from '@testing-library/react';
import TitleInput from '@/entities/questionSet/ui/TitleInput';

describe('TitleInput', () => {
  test('TitleInput이 렌더링 된다.', () => {
    render(<TitleInput displayTitle="title" />);
    expect(
      screen.getByRole('textbox', { name: 'title-input' }),
    ).toBeInTheDocument();
  });
});
