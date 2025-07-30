import MediaStreamViewer from '@/components/practicePage/MediaStreamViewer';
import useMediaStreamStore from '@/stores/useMediaStreamStore';

function MainMediaStreamViewer() {
  const mainStreamType = useMediaStreamStore(state => state.mainStreamType);
  return (
    <div aria-label="main-media-stream-viewer" className="h-full w-1/2">
      <MediaStreamViewer type={mainStreamType} />
    </div>
  );
}

export default MainMediaStreamViewer;
