import MediaStreamViewer from '@/components/practicePage/MediaStreamViewer';
import useMediaStreamStore from '@/stores/useMediaStreamStore';

function SubMediaStreamViewer() {
  const subStreamType = useMediaStreamStore(state => state.subStreamType);
  return (
    <div
      aria-label="sub-media-stream-viewer"
      className="relative h-full flex-[0.8] overflow-hidden rounded-xl bg-black shadow-md"
    >
      <MediaStreamViewer type={subStreamType} />
    </div>
  );
}
export default SubMediaStreamViewer;
