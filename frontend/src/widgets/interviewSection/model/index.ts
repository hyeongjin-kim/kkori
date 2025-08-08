import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';

export function switchScreen() {
  useMediaStreamStore
    .getState()
    .setMainStreamType(
      useMediaStreamStore.getState().mainStreamType === 'my' ? 'peer' : 'my',
    );
  useMediaStreamStore
    .getState()
    .setSubStreamType(
      useMediaStreamStore.getState().subStreamType === 'my' ? 'peer' : 'my',
    );
}

export function startAnswer() {
  const myMediaStream = useMediaStreamStore.getState().myStream;
  if (!myMediaStream) return;
  const recorder = new MediaRecorder(myMediaStream);
  useMediaStreamStore.getState().setMyRecorder(recorder);
  let data: BlobPart[] = [];
  recorder.ondataavailable = event => {
    data.push(event.data);
  };
  recorder.onstop = () => {
    const blob = new Blob(data, { type: 'audio/webm' });
    const url = URL.createObjectURL(blob);
  };
  recorder.start();
  setTimeout(() => {
    if (recorder.state === 'recording') {
      recorder.stop();
    }
  }, 10000);
  usePracticeSessionStore.getState().answerStart();
}

export function endAnswer() {
  console.log('endAnswer');
}

export function endInterview() {
  console.log('endInterview');
}

export function startInterview() {
  usePracticeSessionStore.getState().interviewStart();
}
