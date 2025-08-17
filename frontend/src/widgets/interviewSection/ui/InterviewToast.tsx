import { useEffect } from 'react';
import useInterviewToastStore from '@/widgets/interviewSection/model/useInterviewToastStore';

function InterviewToast() {
  const { toasts, removeToast } = useInterviewToastStore();
  useEffect(() => {
    const timers = toasts.map(t => setTimeout(() => removeToast(t.id), 2000));

    return () => {
      timers.forEach(timer => clearTimeout(timer));
    };
  }, [toasts, removeToast]);

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
