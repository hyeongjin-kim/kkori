import { create } from 'zustand';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';

interface PeerConnectionState {
  peerConnection: RTCPeerConnection | null;
}

interface PeerConnectionActions {
  createPeerConnection: (
    onIceCandidate: (candidate: RTCIceCandidate) => void,
  ) => RTCPeerConnection;
  setPeerConnection: (peerConnection: RTCPeerConnection) => void;
  clearConnections: () => void;
}

const initialState: PeerConnectionState = {
  peerConnection: null,
};

const usePeerConnectionStore = create<
  PeerConnectionState & PeerConnectionActions
>(set => ({
  ...initialState,
  createPeerConnection: (
    onIceCandidate: (candidate: RTCIceCandidate) => void,
  ) => {
    const peerConnection = new RTCPeerConnection({
      iceTransportPolicy: 'all',
      iceServers: [
        {
          urls: [
            'turn:kkori.site:3478?transport=udp',
            'turn:kkori.site:3478?transport=tcp',
            'turns:kkori.site:5349?transport=tcp',
          ],
          username: process.env.TURN_USERNAME || '',
          credential: process.env.TURN_CREDENTIAL || '',
        },
      ],
    });
    peerConnection.onicecandidate = event => {
      if (!event.candidate) return;
      console.log('ICE CANDIDATE : ', event);
      onIceCandidate(event.candidate);
    };
    peerConnection.ontrack = event => {
      const incoming = event.streams?.[0] ?? new MediaStream([event.track]);
      const { peerStream, setPeerStream, setIsPeerVideoOn, setIsPeerAudioOn } =
        useMediaStreamStore.getState();
      if (peerStream) {
        const sameKind = peerStream
          .getTracks()
          .find(t => t.kind === event.track.kind);
        if (sameKind) peerStream.removeTrack(sameKind);
        peerStream.addTrack(event.track);
        setIsPeerVideoOn(true);
        setIsPeerAudioOn(true);
        setPeerStream(peerStream);
      } else {
        setPeerStream(incoming);
      }
    };
    peerConnection.addEventListener('icecandidateerror', (e: any) => {
      console.log('icecandidateerror', {
        url: e.url,
        errorText: e.errorText,
        errorCode: e.errorCode,
        hostCandidate: e.hostCandidate,
        address: e.address,
        port: e.port,
      });
    });
    return peerConnection;
  },
  setPeerConnection: peerConnection => set({ peerConnection }),
  clearConnections: () => {
    set({ peerConnection: null });
  },
}));

export default usePeerConnectionStore;
