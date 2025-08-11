import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';

const MAX_RECORD_TIME = 600000;

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
  usePracticeSessionStore.getState().answerStart();
  const myMediaStream = useMediaStreamStore.getState().myStream;
  if (!myMediaStream) return;
  const recorder = useMediaStreamStore.getState().myRecorder;
  if (!recorder) return;
  let data: BlobPart[] = [];
  recorder.ondataavailable = event => {
    data.push(event.data);
  };
  recorder.onstop = async () => {
    const blob = new Blob(data, { type: 'audio/webm' });
    downloadBlob(blob, 'answer.webm');
    useMediaStreamStore.getState().setData([]);
    usePracticeSessionStore.getState().answerSubmit(blob);
  };
  recorder.start();
  const timerId = setTimeout(() => {
    if (recorder.state === 'recording') {
      recorder.stop();
    }
  }, MAX_RECORD_TIME);
  useMediaStreamStore.getState().setTimerId(timerId);
}

export function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename; // 예: 'answer.webm'
  a.style.display = 'none';
  document.body.appendChild(a);
  a.click();
  // Safari 등 호환을 위해 DOM에 붙였다가 제거
  setTimeout(() => {
    URL.revokeObjectURL(url);
    a.remove();
  }, 0);
}

export function endAnswer() {
  const { myRecorder, timerId } = useMediaStreamStore.getState();

  console.log(
    'endAnswer 호출됨. recorder=',
    !!myRecorder,
    'state=',
    myRecorder?.state,
  );

  if (!myRecorder) return;

  if (timerId) {
    clearTimeout(timerId);
    useMediaStreamStore.getState().setTimerId(null);
  }

  if (myRecorder.state === 'recording') {
    myRecorder.stop();
  }

  console.log('답변 종료 까지 올까?');
}

export function endInterview() {
  usePracticeSessionStore.getState().interviewEnd();
}

export function startInterview() {
  usePracticeSessionStore.getState().interviewStart();
}

export function chooseTailQuestion() {
  console.log('tail-question');
}

export function chooseDefaultQuestion() {
  console.log('default-question');
}

export function chooseCustomQuestion() {
  console.log('custom-question');
}
