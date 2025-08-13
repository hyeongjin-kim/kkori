import { Client } from '@stomp/stompjs';
import useMediaStreamStore from '@/widgets/interviewSection/model/useMediaStreamStore';
import useInterviewRoomStore, {
  interviewStatus,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import {
  Question,
  useInterviewQuestionStore,
} from '@/widgets/interviewSection/model/useInterviewQuestionStore';

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

const existingUserHandler = async (
  client: Client,
  get: any,
  set: any,
  data: any,
) => {
  subscribeInterview(client, get, get().roomId || '');
  get().setOpponentNickname(data.nickName);
  console.log(data.nickName);
  const peerConnection = new RTCPeerConnection({
    iceServers: [
      {
        urls: process.env.TURN_URL || '',
        username: process.env.TURN_USERNAME || '',
        credential: process.env.TURN_CREDENTIAL || '',
      },
    ],
  });
  const myStream = useMediaStreamStore.getState().myStream;
  console.log('myStream', myStream);
  if (!myStream) return;

  myStream.getTracks().forEach(track => {
    peerConnection.addTrack(track, myStream);
  });

  const offer = await peerConnection.createOffer();
  await peerConnection.setLocalDescription(offer);
  const roomId = get().roomId;
  console.log('OFFER : ', offer);
  console.log('ROOM ID : ', roomId);
  get().setPeerConnection(peerConnection);
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
  const peerConnection = new RTCPeerConnection({
    iceServers: [
      {
        urls: process.env.TURN_URL || '',
        username: process.env.TURN_USERNAME || '',
        credential: process.env.TURN_CREDENTIAL || '',
      },
    ],
  });
  const myStream = useMediaStreamStore.getState().myStream;
  if (!myStream) return;
  myStream.getTracks().forEach(track => {
    peerConnection.addTrack(track, myStream);
  });
  const offer = JSON.parse(data);
  await peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
  const answer = await peerConnection.createAnswer();
  await peerConnection.setLocalDescription(answer);

  console.log('PEER CONNECTION : ', peerConnection);

  get().setPeerConnection(peerConnection);
  client.publish({
    destination: `/app/create-answer`,
    body: JSON.stringify({
      roomId: get().roomId,
      sdp: JSON.stringify(answer),
    }),
  });
};

const answerHandler = (client: Client, get: any, set: any, data: any) => {
  const answer = JSON.parse(data);
  console.log(answer);
  get().peerConnection.setRemoteDescription(answer);
  useMediaStreamStore.getState().setPeerStream(answer.sdp);
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

const errorHandler = (client: Client, set: any, data: any) => {
  const errorMessage = data.error;
};
