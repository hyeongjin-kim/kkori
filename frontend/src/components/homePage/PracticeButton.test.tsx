// TO DO
//버튼을 누르면 어떤 페이지로 이동한다
//버튼에 내가 원하는 텍스트를 넣을 수 있다.

import { fireEvent, render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import PracticeButton from "./PracticeButton";

test('버튼을 누르면 어떤 페이지로 이동한다', () => {
  render(
    <BrowserRouter>
      <PracticeButton text="테스트" path="/test" />
    </BrowserRouter>
  );
  fireEvent.click(screen.getByRole('button', { name: /테스트/i }));
  expect(window.location.pathname).toBe('/test');
});

test('버튼에 내가 원하는 텍스트를 넣을 수 있다.', () => {
  const text = '테스트';
  render(
    <BrowserRouter>
      <PracticeButton text={text} path="/test" />
    </BrowserRouter>
  );
  expect(screen.getByRole('button', { name: text })).toBeInTheDocument();
});
