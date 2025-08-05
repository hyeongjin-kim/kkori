import { fireEvent, render, screen } from '@testing-library/react';
import {
  PRACTICE_MODE,
  PracticeButton,
} from '@components/homePage/PracticeButton';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';

test('버튼을 누르면 어떤 페이지로 이동한다', () => {
  render(
    <MemoryRouterWrapped
      component={
        <PracticeButton
          text="연습하기"
          path="/solo-practice"
          mode={PRACTICE_MODE.SOLO_PRACTICE}
        />
      }
    />,
  );
  fireEvent.click(screen.getByRole('button', { name: /연습하기/i }));
  expect(
    screen.getByRole('main', { name: 'solo-practice-page' }),
  ).toBeInTheDocument();
});

test('버튼에 내가 원하는 텍스트를 넣을 수 있다.', () => {
  const text = '테스트';
  render(
    <MemoryRouterWrapped
      component={
        <PracticeButton
          text={text}
          path="/test"
          mode={PRACTICE_MODE.SOLO_PRACTICE}
        />
      }
    />,
  );
  expect(screen.getByRole('button', { name: text })).toBeInTheDocument();
});
