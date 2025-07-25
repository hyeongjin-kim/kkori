import { fireEvent, render, screen } from '@testing-library/react';
import SoloPracticeButton from '@components/homePage/SoloPracticeButton';
import BrowserRouterWrapped from '@components/common/BrowserRouterWrapped';

test('혼자 연습하기 버튼이 렌더링 된다', () => {
  render(<BrowserRouterWrapped component={<SoloPracticeButton />} />);
  expect(
    screen.getByRole('button', { name: /혼자 연습하기/i }),
  ).toBeInTheDocument();
});

test('혼자 연습하기 버튼을 클릭하면 혼자 연습하기 페이지로 이동한다', () => {
  render(<BrowserRouterWrapped component={<SoloPracticeButton />} />);
  const button = screen.getByRole('button', { name: /혼자 연습하기/i });
  fireEvent.click(button);
  expect(window.location.pathname).toBe('/solo-practice');
});
