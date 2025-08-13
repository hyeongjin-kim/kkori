import { render, screen } from '@testing-library/react';
import SelectQuestionSetModal from '@/pages/homePage/ui/SelectQuestionSetModal';
import { createRef } from 'react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('SelectQuestionSetModal', () => {
  const onClose = jest.fn();
  const contentRef = createRef<HTMLDivElement>();
  test('SelectQuestionSetModal이 렌더링 된다', () => {
    render(
      <MemoryRouterWrapped
        component={
          <SelectQuestionSetModal onClose={onClose} contentRef={contentRef} />
        }
      />,
    );
    expect(screen.getByLabelText('modal')).toBeInTheDocument();
  });
});
