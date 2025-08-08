import { render, screen } from '@testing-library/react';
import LabeledTextAreaField from '@/shared/ui/LabeledTextAreaField';

describe('LabeledTextAreaField', () => {
  test('LabeledTextAreaField이 렌더링 된다.', () => {
    render(
      <LabeledTextAreaField
        displayTitle="title"
        label="title-textarea"
        placeholder="title-textarea"
      />,
    );
    expect(
      screen.getByRole('textbox', { name: 'title-textarea' }),
    ).toBeInTheDocument();
  });
});
