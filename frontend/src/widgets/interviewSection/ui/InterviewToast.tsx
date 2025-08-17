import { useEffect } from 'react';
import useInterviewToastStore from '../model/useInterviewToastStore';

function InterviewToast() {
  const { toasts, removeToast } = useInterviewToastStore();
  useEffect(() => {
    if (toasts.length > 0) {
      const timer = setTimeout(() => removeToast(toasts[0].id), 2000);
      return () => clearTimeout(timer);
    }
  }, [toasts]);

  return (
    <div
      aria-label="interview-toast"
      className="fixed bottom-20 left-1/2 z-20 flex -translate-x-1/2 flex-col gap-2"
    >
      {toasts.map(toast => (
        <div
          key={toast.id}
          className="animate-fadeInUp rounded-lg bg-black px-6 py-4 text-center text-white shadow-lg"
        >
          {toast.message}
        </div>
      ))}
    </div>
  );
}

export default InterviewToast;
