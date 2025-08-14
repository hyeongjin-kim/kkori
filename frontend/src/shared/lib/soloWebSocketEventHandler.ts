import { Client } from '@stomp/stompjs';
import useInterviewRoomStore, {
  interviewStatus,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import {
  Question,
  useInterviewQuestionStore,
} from '@/widgets/interviewSection/model/useInterviewQuestionStore';

export const soloWebSocketEventHandler = (
  client: Client,
  get: any,
  set: any,
  response: any,
) => {
  switch (response.type) {
    case 'room-created':
      roomCreatedHandler(client, get, set, response.data);
      break;
    case 'room-status':
      roomStatusHandler(client, set, response.data);
      break;
    case 'next-question-choices':
      nextQuestionChoiceHandler(client, set, response.data);
      break;
    case 'interview-ended':
      interviewEndedHandler(client, set, response.data);
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
  client.subscribe(`/topic/interview/${response.roomId}`, message => {
    const response = JSON.parse(message.body);
    console.log(response);
    get().InterviewMessageHandler(client, response);
  });
};

const roomStatusHandler = (client: Client, set: any, data: any) => {
  console.log(data);
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

const interviewEndedHandler = (client: Client, set: any, data: any) => {
  useInterviewRoomStore.getState().setStatus('endInterview');
};
