import { useCallback, useEffect, useRef, useState } from 'react';

type UseModalOptions = {
  closeOnEscape?: boolean;
  closeOnOutsideClick?: boolean;
  lockScroll?: boolean;
};

export function useModal(options: UseModalOptions = {}) {
  const {
    closeOnEscape = true,
    closeOnOutsideClick = true,
    lockScroll = true,
  } = options;

  const [isOpen, setIsOpen] = useState(false);
  const contentRef = useRef<HTMLDivElement | null>(null);

  const open = useCallback(() => setIsOpen(true), []);
  const close = useCallback(() => setIsOpen(false), []);

  useEffect(() => {
    if (!isOpen || !closeOnEscape) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') close();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [isOpen, closeOnEscape, close]);

  useEffect(() => {
    if (!isOpen || !closeOnOutsideClick) return;
    const onMouseDown = (e: MouseEvent) => {
      const el = contentRef.current;
      if (!el) return;
      if (!el.contains(e.target as Node)) {
        close();
      }
    };
    document.addEventListener('mousedown', onMouseDown, true);
    return () => document.removeEventListener('mousedown', onMouseDown, true);
  }, [isOpen, closeOnOutsideClick, close]);

  useEffect(() => {
    if (!lockScroll) return;
    const original = document.body.style.overflow;
    if (isOpen) document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = original;
    };
  }, [isOpen, lockScroll]);

  return {
    isOpen,
    open,
    close,
    contentRef,
  };
}
