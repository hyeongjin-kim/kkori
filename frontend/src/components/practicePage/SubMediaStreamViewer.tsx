import MediaStreamViewer from '@/components/practicePage/MediaStreamViewer';
import useMediaStreamStore from '@/stores/useMediaStreamStore';

function SubMediaStreamViewer() {
  const subStreamType = useMediaStreamStore(state => state.subStreamType);
  return (
    <div aria-label="sub-media-stream-viewer" className="h-full">
      <MediaStreamViewer type={subStreamType} />
    </div>
  );
}
export default SubMediaStreamViewer;
