import { Link } from 'react-router-dom';

interface GoToButtonProps {
  to: string;
  label: string;
  icon?: React.ReactNode;
  text?: string;
}

function GoToButton({ to, label, icon, text }: GoToButtonProps) {
  return (
    <Link
      to={to}
      aria-label={label}
      className="ml-auto block rounded-full bg-blue-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 active:scale-[0.99]"
    >
      <div className="flex items-center justify-center">
        {icon && <span>{icon}</span>}
        {text && <span>{text}</span>}
      </div>
    </Link>
  );
}

export default GoToButton;
