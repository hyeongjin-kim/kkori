import { render, screen } from '@testing-library/react';
import HomePage from './index';
import { BrowserRouter } from 'react-router-dom';

test('메인 페이지에 혼자 연습하기 버튼이 렌더링 된다', () => {
  render(
    <BrowserRouter>
      <HomePage />
    </BrowserRouter>
  );
  expect(
    screen.getByRole('button', { name: /혼자 연습하기/i })
  ).toBeInTheDocument();
});

test('메인 페이지에 같이 연습하기 버튼이 렌더링 된다', () =>{
  render(
    <BrowserRouter>
      <HomePage />
    </BrowserRouter>
  );
  expect(
    screen.getByRole('button', {name: /같이 연습하기/i})
  ).toBeInTheDocument();
  
})