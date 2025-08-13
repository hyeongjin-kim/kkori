import { fireEvent, render, screen } from '@testing-library/react';
import SoloPracticeButton from '@/pages/homePage/ui/SoloPracticeButton';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('SoloPracticeButton', () => {
  const onClick = jest.fn();
  beforeEach(() => {
    render(
      <MemoryRouterWrapped
        component={<SoloPracticeButton onClick={onClick} />}
      />,
    );
  });
  test('혼자 연습하기 버튼이 렌더링 된다', () => {
    expect(
      screen.getByRole('button', { name: /혼자 연습하기/i }),
    ).toBeInTheDocument();
  });

  test('혼자 연습하기 버튼을 클릭하면 이벤트가 발생한다', () => {
    const button = screen.getByRole('button', { name: /혼자 연습하기/i });
    fireEvent.click(button);
    expect(onClick).toHaveBeenCalled();
  });
});
