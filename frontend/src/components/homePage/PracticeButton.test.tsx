import { fireEvent, render, screen } from '@testing-library/react';
import PracticeButton from './PracticeButton';
import BrowserRouterWrapped from '../common/BrowserRouterWrapped';

test('버튼을 누르면 어떤 페이지로 이동한다', () => {
  render(
    <BrowserRouterWrapped
      component={<PracticeButton text="테스트" path="/test" />}
    />,
  );
  fireEvent.click(screen.getByRole('button', { name: /테스트/i }));
  expect(window.location.pathname).toBe('/test');
});

test('버튼에 내가 원하는 텍스트를 넣을 수 있다.', () => {
  const text = '테스트';
  render(
    <BrowserRouterWrapped
      component={<PracticeButton text={text} path="/test" />}
    />,
  );
  expect(screen.getByRole('button', { name: text })).toBeInTheDocument();
});
