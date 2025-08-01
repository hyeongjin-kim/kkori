import Logo from '@components/common/Logo';

function HeroText() {
  return (
    <div
      aria-label="hero-text"
      className="text-text-white flex flex-col gap-4 text-4xl font-bold"
    >
      <p>꼬리에 꼬리를 무는 면접 질문</p>
      <div className="flex items-center">
        <p className="mr-2">이제는</p>
        <Logo className="mt-2 -ml-4 h-10 w-10" />
        <p>와 함께 AI로 연습하세요</p>
      </div>
    </div>
  );
}

export default HeroText;
