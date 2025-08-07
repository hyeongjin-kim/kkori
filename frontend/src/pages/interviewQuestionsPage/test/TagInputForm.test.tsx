jest.mock('@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore');

import { fireEvent, render, screen } from '@testing-library/react';
import TagInputForm from '@/pages/interviewQuestionsPage/ui/TagInputForm';
import userEvent from '@testing-library/user-event';
import useQuestionSetFilterStore from '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore';

describe('TagInputForm', () => {
  const setSelectedTagMock = jest.fn();
  beforeEach(() => {
    const mockedUseQuestionSetFilterStore =
      useQuestionSetFilterStore as unknown as jest.Mock;
    mockedUseQuestionSetFilterStore.mockReturnValue({
      setSelectedTag: setSelectedTagMock,
      selectedTag: 'TEST',
    });
    render(<TagInputForm />);
  });

  test('form submit 시 setSelectedTag가 호출된다', async () => {
    const input = screen.getByLabelText('tag-input');
    const form = screen.getByRole('form', { name: 'tag-input-form' });

    await userEvent.type(input, '성공');
    fireEvent.submit(form);

    expect(setSelectedTagMock).toHaveBeenCalledWith('성공');
  });

  test('TagInputForm 컴포넌트가 렌더링되어야 합니다.', () => {
    expect(
      screen.getByRole('form', { name: 'tag-input-form' }),
    ).toBeInTheDocument();
    expect(screen.getByLabelText('tag-input')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '조회' })).toBeInTheDocument();
  });
});
