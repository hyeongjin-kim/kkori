import { useEffect, useState } from 'react';
import { usePracticeSessionStore } from './usePracticeSessionStore';

export function usePracticeSessionHydrated() {
  const hasHydrated = (usePracticeSessionStore as any).persist?.hasHydrated?.();
  const [ok, setOk] = useState(!!hasHydrated);
  useEffect(() => {
    const unsub = (usePracticeSessionStore as any).persist?.onFinishHydration?.(
      () => setOk(true),
    );
    if ((usePracticeSessionStore as any).persist?.hasHydrated?.()) setOk(true);
    return unsub;
  }, []);
  return ok;
}
