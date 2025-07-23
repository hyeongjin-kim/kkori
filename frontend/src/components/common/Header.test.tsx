import { render, screen } from '@testing-library/react';
import Header from './Header';
import MemoryRouterWrapped from './MemoryRouterWrapped';

describe('헤더', () => {
  beforeEach(() => {
    render(<MemoryRouterWrapped component={<Header />} />);
  });

  test('헤더에 로고가 렌더링 된다.', () => {
    expect(
      screen.getByRole('img', { name: '로고 이미지' }),
    ).toBeInTheDocument();
  });

  describe('네비게이션 바', () => {
    test('텍스트가 렌더링 된다.', () => {
      expect(screen.getByRole('link', { name: '홈' })).toBeInTheDocument();
    });
  });

  describe('로그인 버튼', () => {
    test('로그인 버튼이 렌더링 된다.', () => {
      expect(screen.getByRole('link', { name: '로그인' })).toBeInTheDocument();
    });
  });
});
