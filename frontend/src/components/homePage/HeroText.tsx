import Logo from '@components/common/Logo';

function HeroText() {
  return (
    <div
      aria-label="hero-text"
      className="text-text-white flex flex-col gap-4 text-4xl font-bold"
    >
      <p>꼬리에 꼬리를 무는 면접 질문</p>
      <p>
        이제는{' '}
        <Logo textClassName="text-4xl" imgClassName="h-10 w-10 -ml-4 mt-2" />와
        함께 AI로 연습하세요
      </p>
    </div>
  );
}

export default HeroText;
