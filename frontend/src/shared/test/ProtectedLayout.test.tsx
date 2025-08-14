import { render, screen, waitFor } from '@testing-library/react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import ProtectedLayout from '@/shared/ui/ProtectedLayout';
import { Route, Routes } from 'react-router-dom';

function AppUnderTest() {
  return (
    <MemoryRouterWrapped
      component={
        <Routes>
          <Route element={<ProtectedLayout />}>
            <Route
              path="/"
              element={<main aria-label="protected-area">protected</main>}
            />
          </Route>
          <Route
            path="/login"
            element={<main aria-label="login-page">login</main>}
          />
        </Routes>
      }
    />
  );
}

describe('ProtectedRoute', () => {
  test('로그인이 되면 자식이 렌더링 된다', async () => {
    jest.mock('@/features/auth/api/request', () => ({
      getMe: jest.fn().mockResolvedValue({
        data: {
          data: {
            nickname: 'test',
            userId: 1,
          },
        },
      }),
    }));
    render(<AppUnderTest />);
    await waitFor(() => {
      expect(
        screen.getByRole('main', { name: 'protected-area' }),
      ).toBeInTheDocument();
    });
  });
  test('로그인이 안되면 로그인페이지로 이동한다.', async () => {
    jest.mock('@/features/auth/api/request', () => ({
      getMe: jest.fn().mockRejectedValueOnce(new Error('403 Forbidden')),
    }));
    render(<AppUnderTest />);
    await waitFor(() => {
      expect(
        screen.getByRole('main', { name: 'login-page' }),
      ).toBeInTheDocument();
    });
  });
});
