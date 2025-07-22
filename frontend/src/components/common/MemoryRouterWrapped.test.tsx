import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from './MemoryRouterWrapped';

test('어떤 컴포넌트를 전달하면 이를 MemoryRouter로 감싼 컴포넌트가 렌더링 된다.', () => {
  const Component = () => {
    return <button>Component</button>;
  };
  render(<MemoryRouterWrapped component={<Component />} />);
  expect(screen.getByRole('button', { name: 'Component' })).toBeInTheDocument();
});
