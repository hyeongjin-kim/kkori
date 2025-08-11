import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';

export function switchScreen() {
  const { mainStreamType, subStreamType, setMainStreamType, setSubStreamType } =
    useMediaStreamStore.getState();
  const switchStreamType = (streamType: 'my' | 'peer') =>
    streamType === 'my' ? 'peer' : 'my';

  setMainStreamType(switchStreamType(mainStreamType));
  setSubStreamType(switchStreamType(subStreamType));
}

export function startAnswer() {
  if (
    useMediaStreamStore.getState().myRecorder === null ||
    useMediaStreamStore.getState().myRecorder?.state !== 'inactive'
  )
    return;
  const myMediaStream = useMediaStreamStore.getState().myStream;
  if (!myMediaStream) return;
  console.log('미디어 스트림 준비');
  const recorder = new MediaRecorder(myMediaStream);
  useMediaStreamStore.getState().setMyRecorder(recorder);
  let data: BlobPart[] = [];
  recorder.ondataavailable = event => {
    data.push(event.data);
  };
  recorder.onstop = () => {
    const blob = new Blob(data, { type: 'audio/webm' });
    const reader = new FileReader();
    let base64data = '';
    reader.onload = () => {
      base64data = reader.result as string;
    };
    reader.readAsDataURL(blob);
    useMediaStreamStore.getState().setData([]);
    usePracticeSessionStore.getState().answerSubmit(base64data);
    console.log(base64data);
  };
  recorder.start();
  console.log('미디어 스트림 녹화 시작');
  setTimeout(() => {
    if (recorder.state === 'recording') {
      recorder.stop();
    }
  }, 10000);
  usePracticeSessionStore.getState().answerStart();
}

export function endAnswer() {
  console.log(useMediaStreamStore.getState().myRecorder);
  console.log(useMediaStreamStore.getState().myRecorder?.state);
  if (
    useMediaStreamStore.getState().myRecorder === null ||
    useMediaStreamStore.getState().myRecorder?.state !== 'recording'
  )
    return;
  useMediaStreamStore.getState().myRecorder?.stop();
}
console.log('endAnswer');
export function endInterview() {
  usePracticeSessionStore.getState().interviewEnd();
}

export function startInterview() {
  usePracticeSessionStore.getState().interviewStart();
}
