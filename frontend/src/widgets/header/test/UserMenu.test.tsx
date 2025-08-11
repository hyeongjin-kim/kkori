import { render, screen } from '@testing-library/react';
import UserMenu from '@/widgets/header/ui/UserMenu';
import { QueryProvider } from '@/app/providers/QueryProvider';

describe('UserMenu', () => {
  test('유저 메뉴가 렌더링된다.', () => {
    render(
      <QueryProvider>
        <UserMenu user={{ data: { nickname: 'test' } }} />
      </QueryProvider>,
    );
    expect(screen.getByLabelText('user-menu')).toBeInTheDocument();
  });
});
