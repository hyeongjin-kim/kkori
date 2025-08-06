import { useLayoutEffect } from 'react';

const useScrollToBottom = ({ contents }: { contents: any }) => {
  useLayoutEffect(() => {
    const list = document.querySelector('[aria-label="scrollable-list"]');
    list?.scrollTo({ top: list.scrollHeight, behavior: 'instant' });
  }, [contents]);
};

function ScrollableList({ children }: { children: React.ReactNode }) {
  useScrollToBottom({ contents: children });

  return (
    <ul
      className="h-full w-full overflow-y-auto px-5"
      aria-label="scrollable-list"
    >
      {children}
    </ul>
  );
}

export default ScrollableList;
