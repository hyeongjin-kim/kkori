type Variant = 'info' | 'success' | 'warning' | 'error';

const styles: Record<Variant, string> = {
  info: 'border-blue-200 bg-blue-50 text-blue-700',
  success: 'border-emerald-200 bg-emerald-50 text-emerald-700',
  warning: 'border-amber-200 bg-amber-50 text-amber-800',
  error: 'border-red-200 bg-red-50 text-red-700',
};

function Alert({
  variant = 'info',
  children,
  className = '',
  role = 'alert',
}: {
  variant?: Variant;
  children: React.ReactNode;
  className?: string;
  role?: 'alert' | 'status';
}) {
  return (
    <div
      role={role}
      className={`rounded-xl border p-4 text-sm ${styles[variant]} ${className}`}
    >
      {children}
    </div>
  );
}

export default Alert;
