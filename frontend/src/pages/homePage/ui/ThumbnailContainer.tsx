function ThumbnailContainer() {
  return (
    <img
      aria-label="thumbnail-container"
      src="/thumbnail.avif"
      alt="thumbnail-container"
      className="z-10 aspect-auto w-120"
      loading="eager"
      decoding="async"
    />
  );
}

export default ThumbnailContainer;
