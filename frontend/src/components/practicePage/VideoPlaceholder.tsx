function VideoPlaceholder({ visible }: { visible: boolean }) {
  return (
    visible && (
      <div
        aria-label="video-placeholder"
        className="flex h-full w-full items-center justify-center"
      >
        <img src="/assets/video-off.svg" alt="video-off-image" />
      </div>
    )
  );
}

export default VideoPlaceholder;
