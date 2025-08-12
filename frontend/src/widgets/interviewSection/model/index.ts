import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';
import { useInterviewQuestionStore } from './useInterviewQuestionStore';
import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';

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
    //downloadBlob(blob, 'answer.webm');
    useMediaStreamStore.getState().setBlob(blob);
    usePracticeSessionStore.getState().answerSubmit();
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
  a.download = filename;
  a.style.display = 'none';
  document.body.appendChild(a);
  a.click();

  setTimeout(() => {
    URL.revokeObjectURL(url);
    a.remove();
  }, 0);
}

export function endAnswer() {
  const { myRecorder, timerId } = useMediaStreamStore.getState();

  if (!myRecorder) return;

  if (timerId) {
    clearTimeout(timerId);
    useMediaStreamStore.getState().setTimerId(null);
  }

  if (myRecorder.state === 'recording') {
    myRecorder.stop();
  }
}

export function endInterview() {
  usePracticeSessionStore.getState().interviewEnd();
}

export function startInterview() {
  usePracticeSessionStore.getState().interviewStart();
}

export function openNextQuestionModal() {
  useInterviewRoomStore.getState().setModalOpen(true);
}

export function chooseTailQuestion(questionIndex: number) {
  useInterviewRoomStore.getState().setModalOpen(false);
  useInterviewQuestionStore
    .getState()
    .setNextQuestion(
      useInterviewQuestionStore.getState().tailQuestion[questionIndex],
    );
  usePracticeSessionStore.getState().nextQuestionSelect();
}

export function chooseDefaultQuestion() {
  useInterviewRoomStore.getState().setModalOpen(false);
  useInterviewQuestionStore
    .getState()
    .setNextQuestion(useInterviewQuestionStore.getState().defaultQuestion);
  usePracticeSessionStore.getState().nextQuestionSelect();
}

export function chooseCustomQuestion() {
  useInterviewRoomStore.getState().setModalOpen(false);
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.CUSTOM_QUESTION_SELECTED);
}

export function startCustomQuestion() {
  if (
    useMediaStreamStore.getState().myRecorder === null ||
    useMediaStreamStore.getState().myRecorder?.state !== 'inactive'
  )
    return;
  usePracticeSessionStore.getState().customQuestionStart();
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
    //downloadBlob(blob, 'answer.webm');
    useMediaStreamStore.getState().setBlob(blob);
    usePracticeSessionStore.getState().customQuestionCreate();
  };

  recorder.start();
  const timerId = setTimeout(() => {
    if (recorder.state === 'recording') {
      recorder.stop();
    }
  }, MAX_RECORD_TIME);
  useMediaStreamStore.getState().setTimerId(timerId);
}

export function endCustomQuestion() {
  const { myRecorder, timerId } = useMediaStreamStore.getState();

  if (!myRecorder) return;

  if (timerId) {
    clearTimeout(timerId);
    useMediaStreamStore.getState().setTimerId(null);
  }

  if (myRecorder.state === 'recording') {
    myRecorder.stop();
  }
}
