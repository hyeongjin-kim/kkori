import { create } from 'zustand';

interface MediaStreamState {
  myStream: MediaStream | null;
  peerStream: MediaStream | null;
  isMyVideoOn: boolean;
  isMyAudioOn: boolean;
  isPeerVideoOn: boolean;
  isPeerAudioOn: boolean;
}

interface MediaStreamActions {
  setMyStream: (myStream: MediaStream) => void;
  setPeerStream: (peerStream: MediaStream) => void;
  setIsMyVideoOn: (isMyVideoOn: boolean) => void;
  setIsMyAudioOn: (isMyAudioOn: boolean) => void;
  setIsPeerVideoOn: (isPeerVideoOn: boolean) => void;
  setIsPeerAudioOn: (isPeerAudioOn: boolean) => void;
  reset: () => void;
}

const initialState: MediaStreamState = {
  myStream: null,
  peerStream: null,
  isMyVideoOn: false,
  isMyAudioOn: false,
  isPeerVideoOn: false,
  isPeerAudioOn: false,
};

const useMediaStreamStore = create<MediaStreamState & MediaStreamActions>(
  set => ({
    ...initialState,
    setMyStream: myStream => set({ myStream }),
    setPeerStream: peerStream => set({ peerStream }),
    setIsMyVideoOn: isMyVideoOn => set({ isMyVideoOn }),
    setIsMyAudioOn: isMyAudioOn => set({ isMyAudioOn }),
    setIsPeerVideoOn: isPeerVideoOn => set({ isPeerVideoOn }),
    setIsPeerAudioOn: isPeerAudioOn => set({ isPeerAudioOn }),
    reset: () => set(initialState),
  }),
);

export default useMediaStreamStore;
