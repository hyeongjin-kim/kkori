import { render, screen } from '@testing-library/react';
import AppRouter from '@/app/routes/AppRouter';
import { appRoutes } from '@/app/routes/routes';
import { MemoryRouter } from 'react-router-dom';

describe('AppRouter', () => {
  test('appRoutes의 모든 경로가 렌더링 된다.', () => {
    Object.values(appRoutes).forEach(routes => {
      routes.forEach(route => {
        render(
          <MemoryRouter initialEntries={[route.path]}>
            <AppRouter />
          </MemoryRouter>,
        );
        expect(
          screen.getByRole('main', { name: route.label }),
        ).toBeInTheDocument();
      });
    });
  });
});
