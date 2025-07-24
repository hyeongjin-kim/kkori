import { fireEvent, render, screen } from '@testing-library/react';
import PairPracticeButton from './PairPracticeButton';
import BrowserRouterWrapped from '../common/BrowserRouterWrapped';

test('같이 연습하기 버튼이 렌더링 된다', () => {
  render(<BrowserRouterWrapped component={<PairPracticeButton />} />);
  expect(
    screen.getByRole('button', { name: /같이 연습하기/i }),
  ).toBeInTheDocument();
});
test('같이 연습하기 버튼을 클릭하면 같이 연습하기 페이지로 이동한다', () => {
  render(<BrowserRouterWrapped component={<PairPracticeButton />} />);
  const button = screen.getByRole('button', { name: /같이 연습하기/i });
  fireEvent.click(button);
  expect(window.location.pathname).toBe('/pair-practice');
});
