function BackgroundShadow() {
  return (
    <div aria-label="background-shadow" className="absolute inset-0 z-0">
      <div className="absolute right-[10%] bottom-[10%] h-[600px] w-[600px] rounded-full bg-blue-500 opacity-20 blur-[120px]"></div>
    </div>
  );
}

export default BackgroundShadow;
