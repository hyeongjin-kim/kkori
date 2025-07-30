interface LogoProps {
  textClassName?: string;
  imgClassName?: string;
}

function Logo({ textClassName, imgClassName }: LogoProps) {
  return (
    <div className="inline-flex items-center">
      <span
        className={`bg-gradient-to-b from-[#fef6da] to-[#e08311] bg-clip-text text-2xl font-extrabold text-transparent ${textClassName}`}
      >
        꼬리
      </span>
      <img
        src="/fox-tail.png"
        alt="꼬리"
        className={`inline-block ${imgClassName}`}
        aria-label="logo-image"
      />
    </div>
  );
}

export default Logo;
