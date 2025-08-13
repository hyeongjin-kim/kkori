import JoinInterviewRoomModal from '@/pages/homePage/ui/JoinInterviewRoomModal';
import { render, screen } from '@testing-library/react';
import { createRef } from 'react';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('JoinInterviewRoomModal', () => {
  const onClose = jest.fn();
  const onCreate = jest.fn();
  const contentRef = createRef<HTMLDivElement>();
  test('JoinInterviewRoomModal이 렌더링 된다', () => {
    render(
      <MemoryRouterWrapped
        component={
          <JoinInterviewRoomModal
            onClose={onClose}
            onCreate={onCreate}
            contentRef={contentRef}
          />
        }
      />,
    );
    expect(screen.getByLabelText('modal')).toBeInTheDocument();
  });
});
