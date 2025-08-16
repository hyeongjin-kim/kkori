import { useEffect, useState } from 'react';
import useInterviewRoomStore from './useInterviewRoomStore';

export function useInterviewRoomHydrated() {
  const hasHydrated = (useInterviewRoomStore as any).persist?.hasHydrated?.();
  const [ok, setOk] = useState(!!hasHydrated);
  useEffect(() => {
    const unsub = (useInterviewRoomStore as any).persist?.onFinishHydration?.(
      () => setOk(true),
    );
    if ((useInterviewRoomStore as any).persist?.hasHydrated?.()) setOk(true);
    return unsub;
  }, []);
  return ok;
}
