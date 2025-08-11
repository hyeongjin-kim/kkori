function SectionCard({
  title,
  children,
  right,
  ariaLabel,
}: {
  title: string;
  children: React.ReactNode;
  right?: React.ReactNode;
  ariaLabel: string;
}) {
  return (
    <section
      aria-label={ariaLabel}
      className="w-full rounded-2xl border border-gray-200 bg-white p-5 shadow-sm"
    >
      <div className="mb-3 flex items-center justify-between">
        <h2 className="text-base font-semibold text-gray-900">{title}</h2>
        {right}
      </div>
      {children}
    </section>
  );
}

export default SectionCard;
