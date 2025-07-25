import { fireEvent, render, screen } from '@testing-library/react';
import SoloPracticeButton from '@components/homePage/SoloPracticeButton';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';

describe('SoloPracticeButton', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<SoloPracticeButton />} />);
  });
  test('혼자 연습하기 버튼이 렌더링 된다', () => {
    expect(
      screen.getByRole('button', { name: /혼자 연습하기/i }),
    ).toBeInTheDocument();
  });

  test('혼자 연습하기 버튼을 클릭하면 혼자 연습하기 페이지로 이동한다', () => {
    const button = screen.getByRole('button', { name: /혼자 연습하기/i });
    fireEvent.click(button);
    expect(
      screen.getByRole('main', { name: 'solo-practice-page' }),
    ).toBeInTheDocument();
  });
});
