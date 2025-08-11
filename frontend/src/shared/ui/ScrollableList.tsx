import { useLayoutEffect, useRef } from 'react';

interface useScrollToBottomProps {
  dependencies: any[];
  scrollRef: React.RefObject<HTMLElement | null>;
}

interface ScrollableListProps {
  children: React.ReactNode;
}

const useScrollToBottom = ({
  dependencies,
  scrollRef,
}: useScrollToBottomProps) => {
  useLayoutEffect(() => {
    const scroll = scrollRef.current;
    scroll?.scrollTo({ top: scroll.scrollHeight });
  }, dependencies);
};

function ScrollableList({ children }: ScrollableListProps) {
  const listRef = useRef<HTMLUListElement>(null);
  useScrollToBottom({
    dependencies: [children],
    scrollRef: listRef,
  });

  return (
    <ul
      className="h-full w-full overflow-y-auto px-5"
      aria-label="scrollable-list"
      ref={listRef}
    >
      {children}
    </ul>
  );
}

export default ScrollableList;
