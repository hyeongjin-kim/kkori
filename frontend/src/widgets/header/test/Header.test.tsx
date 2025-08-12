jest.mock('@/features/auth/api/me', () => ({
  useMe: jest.fn(),
}));

import { render, screen } from '@testing-library/react';
import Header from '@/widgets/header';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

import * as meApi from '@/features/auth/api/me';
const mockedUseMe = meApi.useMe as jest.Mock;

describe('Header', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  const renderHeader = () =>
    render(<MemoryRouterWrapped component={<Header />} />);

  test('헤더에 로고가 렌더링 된다.', () => {
    mockedUseMe.mockReturnValue({
      data: null,
      isLoading: false,
      isError: false,
    });
    renderHeader();
    expect(screen.getByRole('img', { name: 'logo-image' })).toBeInTheDocument();
  });

  test('로딩 중이면 스켈레톤이 보인다', () => {
    mockedUseMe.mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    });
    renderHeader();
    expect(screen.getByLabelText('login-button-skeleton')).toBeInTheDocument();
    expect(
      screen.queryByRole('link', { name: 'login-button' }),
    ).not.toBeInTheDocument();
  });

  test('비로그인 상태면 로그인 버튼이 보인다', () => {
    mockedUseMe.mockReturnValue({
      data: null,
      isLoading: false,
      isError: false,
    });
    renderHeader();
    expect(
      screen.getByRole('link', { name: 'login-button' }),
    ).toBeInTheDocument();
  });

  test('로그인 상태면 유저 메뉴가 보인다', () => {
    mockedUseMe.mockReturnValue({
      data: { data: { nickname: '이찬' } },
      isLoading: false,
      isError: false,
    });
    renderHeader();
    expect(screen.getByText(/이찬/)).toBeInTheDocument();
    expect(screen.getByLabelText('logout-button')).toBeInTheDocument();
  });
});
