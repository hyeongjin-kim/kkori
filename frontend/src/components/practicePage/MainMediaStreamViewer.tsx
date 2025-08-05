import MediaStreamViewer from '@/components/practicePage/MediaStreamViewer';
import useMediaStreamStore from '@/stores/useMediaStreamStore';

function MainMediaStreamViewer() {
  const mainStreamType = useMediaStreamStore(state => state.mainStreamType);
  return (
    <div
      aria-label="main-media-stream-viewer"
      className="relative h-full flex-[1.2] overflow-hidden rounded-xl bg-black shadow-md"
    >
      <MediaStreamViewer type={mainStreamType} />
    </div>
  );
}

export default MainMediaStreamViewer;
