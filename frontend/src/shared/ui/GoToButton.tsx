import { Link } from 'react-router-dom';

interface GoToButtonProps {
  to: string;
  label: string;
  icon?: React.ReactNode;
  text?: string;
  variant?: 'primary' | 'secondary' | 'ghost';
}

function IconArrowRight(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" aria-hidden="true" {...props}>
      <path
        d="M5 12h14M13 5l7 7-7 7"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

function GoToButton({
  to,
  label,
  icon,
  text,
  variant = 'primary',
}: GoToButtonProps) {
  const baseClasses =
    'inline-flex items-center gap-1.5 rounded-xl px-4 py-2 text-sm font-semibold shadow-sm transition active:scale-[0.99] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500/60';

  const variantClasses = {
    primary:
      'bg-blue-600 text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60',
    secondary:
      'border border-blue-600 bg-white text-blue-600 hover:bg-blue-50 disabled:cursor-not-allowed disabled:opacity-60',
    ghost:
      'border border-gray-200 bg-white text-gray-700 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-60',
  };

  const finalIcon = icon ?? <IconArrowRight className="h-4 w-4" />;

  return (
    <Link
      to={to}
      aria-label={label}
      className={`${baseClasses} ${variantClasses[variant]}`}
    >
      {text && <span>{text}</span>}
      {finalIcon}
    </Link>
  );
}

export default GoToButton;
