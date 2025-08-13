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
      iceServers: [
        { urls: ['stun:kkori.site:3478'] },
        {
          urls: [
            'turn:kkori.site:3478?transport=udp',
            'turn:kkori.site:3478?transport=tcp',
            'turns:kkori.site:5349?transport=tcp',
          ],
          username: 'TURN_USERNAME',
          credential: 'TURN_PASSWORD',
        },
      ],
    });
    peerConnection.onicecandidate = event => {
      if (!event.candidate) return;
      console.log('ICE CANDIDATE : ', event);
      onIceCandidate(event.candidate);
    };
    peerConnection.ontrack = event => {
      console.log('TRACK : ', event.streams[0]);
      useMediaStreamStore.getState().setPeerStream(event.streams[0]);
    };
    return peerConnection;
  },
  setPeerConnection: peerConnection => set({ peerConnection }),
  clearConnections: () => {
    set({ peerConnection: null });
  },
}));

export default usePeerConnectionStore;
