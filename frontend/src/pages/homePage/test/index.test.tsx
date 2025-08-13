import { render, screen } from '@testing-library/react';
import HomePage from '@/pages/homePage/page/index';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('HomePage', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<HomePage />} />);
  });

  test('메인 페이지가 렌더링 된다', () => {
    expect(
      screen.getByRole('main', { name: /home-page/i }),
    ).toBeInTheDocument();
  });

  test('메인 페이지에 혼자 연습하기 버튼이 렌더링 된다', () => {
    expect(
      screen.getByRole('button', { name: /혼자 연습하기/i }),
    ).toBeInTheDocument();
  });

  test('메인 페이지에 같이 연습하기 버튼이 렌더링 된다', () => {
    expect(
      screen.getByRole('button', { name: /같이 연습하기\(면접관\)/i }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole('button', { name: /같이 연습하기\(면접자\)/i }),
    ).toBeInTheDocument();
  });

  test('메인 페이지에 썸네일 컨테이너가 렌더링 된다', () => {
    expect(
      screen.getByRole('img', { name: 'thumbnail-container' }),
    ).toBeInTheDocument();
  });
});
