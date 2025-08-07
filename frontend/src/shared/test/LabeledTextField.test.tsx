import { render, screen } from '@testing-library/react';
import LabeledTextField from '@/shared/ui/LabeledTextField';

describe('LabeledTextField', () => {
  test('LabeledTextField이 렌더링 된다.', () => {
    render(
      <LabeledTextField
        displayTitle="title"
        label="title-input"
        placeholder="title-input"
      />,
    );
    expect(
      screen.getByRole('textbox', { name: 'title-input' }),
    ).toBeInTheDocument();
  });
});
