import { fireEvent, render, screen } from '@testing-library/react';
import { PracticeButton } from '@/pages/homePage/ui/PracticeButton';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import {
  interviewRole,
  interviewType,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';

test('버튼을 누르면 이벤트가 발생한다', () => {
  const onClick = jest.fn();
  render(
    <MemoryRouterWrapped
      component={<PracticeButton text="연습하기" onClick={onClick} />}
    />,
  );
  fireEvent.click(screen.getByRole('button', { name: /연습하기/i }));
  expect(onClick).toHaveBeenCalled();
});

test('버튼에 내가 원하는 텍스트를 넣을 수 있다.', () => {
  const text = '테스트';
  render(
    <MemoryRouterWrapped
      component={<PracticeButton text={text} onClick={() => {}} />}
    />,
  );
  expect(screen.getByRole('button', { name: text })).toBeInTheDocument();
});
