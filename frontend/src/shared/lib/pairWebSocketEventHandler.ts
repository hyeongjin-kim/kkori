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
      existingUserHandler(client, set, response.data);
      break;
    case 'joined-user':
      joinedUserHandler(client, set, response.data);
      break;
    case 'room-status':
      roomStatusHandler(client, set, response.data);
      break;
    case 'offer':
      offerHandler(client, set, response.data);
      break;
    case 'answer':
      answerHandler(client, set, response.data);
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
  set({ roomID: response.roomId });
  client.subscribe(`/topic/interview/${response.roomId}`, message => {
    const response = JSON.parse(message.body);
    console.log(response);
    get().InterviewMessageHandler(client, response);
  });
};

const existingUserHandler = async (client: Client, set: any, data: any) => {
  set({ opponentNickname: data.nickname });
  const peerConnection = new RTCPeerConnection();
  const myStream = useMediaStreamStore.getState().myStream;

  if (!myStream) return;

  myStream.getTracks().forEach(track => {
    peerConnection.addTrack(track, myStream);
  });

  const offer = await peerConnection.createOffer();
  await peerConnection.setLocalDescription(offer);
  client.publish({
    destination: `/app/offer`,
    body: JSON.stringify({
      roomId: data.roomId,
      offer: offer,
    }),
  });
};

const joinedUserHandler = (client: Client, set: any, data: any) => {
  set({ opponentNickname: data.nickname });
};

const roomStatusHandler = (client: Client, set: any, data: any) => {
  console.log(data);
};

const offerHandler = async (client: Client, set: any, data: any) => {
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
  peerConnection.setRemoteDescription(data.offer);
  useMediaStreamStore.getState().setPeerStream(data.offer.stream);
  const answer = await peerConnection.createAnswer();
  await peerConnection.setLocalDescription(answer);
  client.publish({
    destination: `/app/answer`,
    body: JSON.stringify({
      roomId: data.roomId,
      answer: answer,
    }),
  });
};

const answerHandler = (client: Client, set: any, data: any) => {
  const peerConnection = new RTCPeerConnection();
  peerConnection.setRemoteDescription(data.answer);
  useMediaStreamStore.getState().setPeerStream(data.answer.stream);
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
