function Logo({ className }: { className?: string }) {
  return (
    <img
      src="/logo.webp"
      alt="logo"
      className={`inline-block ${className}`}
      aria-label="logo-image"
    />
  );
}

export default Logo;
