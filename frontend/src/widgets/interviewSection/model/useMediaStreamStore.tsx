import { create } from 'zustand';
import { MediaStreamType } from '@/widgets/interviewSection/model/types';
interface MediaStreamState {
  myStream: MediaStream | null;
  peerStream: MediaStream | null;
  isMyVideoOn: boolean;
  isMyAudioOn: boolean;
  isPeerVideoOn: boolean;
  isPeerAudioOn: boolean;
  mainStreamType: MediaStreamType;
  subStreamType: MediaStreamType;
}

interface MediaStreamActions {
  setMyStream: (myStream: MediaStream) => void;
  setPeerStream: (peerStream: MediaStream) => void;
  setIsMyVideoOn: (isMyVideoOn: boolean) => void;
  setIsMyAudioOn: (isMyAudioOn: boolean) => void;
  setIsPeerVideoOn: (isPeerVideoOn: boolean) => void;
  setIsPeerAudioOn: (isPeerAudioOn: boolean) => void;
  reset: () => void;
  setMainStreamType: (mainStreamType: MediaStreamType) => void;
  setSubStreamType: (subStreamType: MediaStreamType) => void;
}

const initialState: MediaStreamState = {
  myStream: null,
  peerStream: null,
  isMyVideoOn: false,
  isMyAudioOn: false,
  isPeerVideoOn: false,
  isPeerAudioOn: false,
  mainStreamType: 'my',
  subStreamType: 'peer',
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
    setMainStreamType: mainStreamType => set({ mainStreamType }),
    setSubStreamType: subStreamType => set({ subStreamType }),
  }),
);

export default useMediaStreamStore;
