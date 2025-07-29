function AudioOffDisplay({ visible }: { visible: boolean }) {
  if (!visible) return null;
  return (
    <div aria-label="audio-off-display">
      <img src="/assets/audio-off.svg" alt="audio-off-icon" />
    </div>
  );
}

export default AudioOffDisplay;
