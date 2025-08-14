import { fireEvent, render, screen } from '@testing-library/react';
import PairPracticeButton from '@/pages/homePage/ui/PairPracticeButton';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import { interviewRole } from '@/entities/interviewRoom/model/useInterviewRoomStore';

describe('PairPracticeButton', () => {
  const onClick = jest.fn();
  beforeEach(() => {
    render(
      <MemoryRouterWrapped
        component={<PairPracticeButton onClick={onClick} />}
      />,
    );
  });
  test('같이 연습하기 버튼이 렌더링 된다', () => {
    expect(
      screen.getByRole('button', { name: /같이 연습하기/i }),
    ).toBeInTheDocument();
  });

  test('같이 연습하기 버튼을 클릭하면 같이 연습하기 페이지로 이동한다', () => {
    const button = screen.getByRole('button', { name: /같이 연습하기/i });
    fireEvent.click(button);
    expect(onClick).toHaveBeenCalled();
  });
});
