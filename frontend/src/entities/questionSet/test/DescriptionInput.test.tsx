import { render, screen } from '@testing-library/react';
import DescriptionInput from '@/entities/questionSet/ui/DescriptionInput';

describe('DescriptionInput', () => {
  test('DescriptionInput이 렌더링 된다.', () => {
    render(<DescriptionInput displayTitle="description" />);
    expect(
      screen.getByRole('textbox', { name: 'description-input' }),
    ).toBeInTheDocument();
  });
});
