function Logo({ className }: { className?: string }) {
  return (
    <div className="inline-flex items-center">
      <img
        src="/logo.png"
        alt="logo"
        className={`inline-block ${className}`}
        aria-label="logo-image"
      />
    </div>
  );
}

export default Logo;
