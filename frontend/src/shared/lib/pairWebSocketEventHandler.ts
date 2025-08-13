import { Client } from '@stomp/stompjs';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';
import useInterviewRoomStore, {
  interviewStatus,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import {
  Question,
  useInterviewQuestionStore,
} from '@/widgets/interviewSection/model/useInterviewQuestionStore';
import usePeerConnectionStore from '@/entities/interviewRoom/model/usePeerConnectionStore';

export const pairWebSocketEventHandler = (
  client: Client,
  get: any,
  set: any,
  response: any,
) => {
  switch (response.type) {
    case 'room-created':
      roomCreatedHandler(client, get, set, response.data);
      break;
    case 'existing-user':
      existingUserHandler(client, get, set, response.data);
      break;
    case 'joined-user':
      joinedUserHandler(client, get, set, response.data);
      break;
    case 'room-status':
      roomStatusHandler(client, set, response.data);
      break;
    case 'received-offer':
      offerHandler(client, get, set, response.data);
      break;
    case 'received-answer':
      answerHandler(client, get, set, response.data);
      break;
    case 'next-question-choices':
      nextQuestionChoiceHandler(client, set, response.data);
      break;
    case 'received-ice-candidate':
      console.log('!!!!RECEIVED ICE CANDIDATE 이벤트 발생이라고!!!!: ');
      receivedIceCandidateHandler(client, get, set, response.data);
      break;
    default:
      errorHandler(client, set, response.data);
      break;
  }
};

const roomCreatedHandler = (
  client: Client,
  get: any,
  set: any,
  response: any,
) => {
  get().setRoomId(response.roomId);
  subscribeInterview(client, get, response.roomId);
};

const subscribeInterview = (client: Client, get: any, roomId: string) => {
  client.subscribe(`/topic/interview/${roomId}`, message => {
    const response = JSON.parse(message.body);
    console.log(response);
    get().InterviewMessageHandler(client, response);
  });
};

const createPeerConnection =
  usePeerConnectionStore.getState().createPeerConnection;

const existingUserHandler = async (
  client: Client,
  get: any,
  set: any,
  data: any,
) => {
  subscribeInterview(client, get, get().roomId || '');
  get().setOpponentNickname(data.nickName);
  console.log('OPPONENT NICKNAME : ', data.nickName);
  const roomId = get().roomId;
  const userId = get().userId;
  const onIceCandidate = (candidate: RTCIceCandidate) => {
    client.publish({
      destination: '/app/new-ice-candidate',
      body: JSON.stringify({ roomId, candidate, userId }),
    });
    console.log('new-ice-candidate 이벤트 발생: ', candidate);
  };
  const peerConnection = createPeerConnection(onIceCandidate);

  const myStream = useMediaStreamStore.getState().myStream;
  console.log('MY STREAM : ', myStream);
  if (!myStream) return;

  myStream.getTracks().forEach(track => {
    peerConnection.addTrack(track, myStream);
  });

  const offer = await peerConnection.createOffer();
  await peerConnection.setLocalDescription(offer);
  console.log('OFFER : ', offer);
  console.log('ROOM ID : ', roomId);
  usePeerConnectionStore.getState().setPeerConnection(peerConnection);
  client.publish({
    destination: `/app/create-offer`,
    body: JSON.stringify({
      roomId,
      sdp: JSON.stringify(offer),
    }),
  });
};

const joinedUserHandler = (client: Client, get: any, set: any, data: any) => {
  set({ opponentNickname: data.nickname });
};

const roomStatusHandler = (client: Client, set: any, data: any) => {
  console.log(data);
};

const offerHandler = async (client: Client, get: any, set: any, data: any) => {
  const roomId = get().roomId;
  const userId = get().userId;
  const onIceCandidate = (candidate: RTCIceCandidate) => {
    client.publish({
      destination: '/app/new-ice-candidate',
      body: JSON.stringify({ roomId, candidate, userId }),
    });
  };
  const peerConnection = createPeerConnection(onIceCandidate);
  const myStream = useMediaStreamStore.getState().myStream;
  if (!myStream) return;
  myStream.getTracks().forEach(track => {
    peerConnection.addTrack(track, myStream);
  });
  const offer = JSON.parse(data);
  await peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
  const answer = await peerConnection.createAnswer();
  await peerConnection.setLocalDescription(answer);
  usePeerConnectionStore.getState().setPeerConnection(peerConnection);
  client.publish({
    destination: `/app/create-answer`,
    body: JSON.stringify({
      roomId: roomId,
      sdp: JSON.stringify(answer),
    }),
  });
};

const answerHandler = async (client: Client, get: any, set: any, data: any) => {
  const answer = JSON.parse(data);
  const peerConnection = usePeerConnectionStore.getState().peerConnection;
  console.log('ANSWER HANDLER : ', answer);
  if (!peerConnection) return;
  await peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
  console.log('ANSWER PEER CONNECTION : ', peerConnection);
  usePeerConnectionStore.getState().setPeerConnection(peerConnection);
};

const nextQuestionChoiceHandler = (client: Client, set: any, data: any) => {
  const questions: Question[] = [];
  data.nextQuestionChoices.forEach((choice: any) => {
    questions.push({
      question: choice.questionText,
      id: choice.questionId,
      questionType: choice.questionType,
    });
  });
  useInterviewQuestionStore
    .getState()
    .setTailQuestion([questions[0], questions[1]]);
  useInterviewQuestionStore.getState().setDefaultQuestion(questions[2]);
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.NEXT_QUESTION_PRESENTED);
  console.log(useInterviewRoomStore.getState().status);
};

const receivedIceCandidateHandler = (
  client: Client,
  get: any,
  set: any,
  data: any,
) => {
  const response = JSON.parse(data);
  const peerConnection = usePeerConnectionStore.getState().peerConnection;
  console.log('!!!!RECEIVED ICE CANDIDATE : ', response);
  if (!peerConnection) return;
  peerConnection.addIceCandidate(new RTCIceCandidate(response.candidate));
};

const errorHandler = (client: Client, set: any, data: any) => {
  const errorMessage = data.error;
};
