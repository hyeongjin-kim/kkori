import MediaStreamViewer from '@/components/practicePage/MediaStreamViewer';
import useMediaStreamStore from '@/stores/useMediaStreamStore';

function SubMediaStreamViewer() {
  const subStreamType = useMediaStreamStore(state => state.subStreamType);
  return (
    <div aria-label="sub-media-stream-viewer" className="h-full w-1/2">
      <MediaStreamViewer type={subStreamType} />
    </div>
  );
}
export default SubMediaStreamViewer;
