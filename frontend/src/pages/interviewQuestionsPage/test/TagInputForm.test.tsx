import { render, screen } from '@testing-library/react';
import TagInputForm from '@/pages/interviewQuestionsPage/ui/TagInputForm';

describe('TagInputForm', () => {
  test('TagInputForm 컴포넌트가 렌더링되어야 합니다.', () => {
    render(<TagInputForm />);
    screen.getByRole('form', { name: 'tag-input-form' });
    screen.getByPlaceholderText('태그를 입력해주세요.');
    screen.getByRole('button', { name: '조회' });
  });
});
