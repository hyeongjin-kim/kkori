import { Client } from '@stomp/stompjs';
import { StateCreator } from 'zustand';
import { INTERVIEW_MESSAGE_TYPE } from '@/widgets/interviewSection/model/constants';
import useInterviewRoomStore, {
  interviewStatus,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';
import {
  Question,
  useInterviewQuestionStore,
} from './useInterviewQuestionStore';
import useUserStore from '@/entities/user/model/useUserStore';

interface InterviewState {}

interface InterviewAction {
  InterviewMessageHandler: (client: Client, response: any) => void;
}

export interface InterviewSlice extends InterviewState, InterviewAction {}

const initialState: InterviewState = {
  status: 'BEFORE_INTERVIEW',
};

export const createInterviewSlice: StateCreator<
  InterviewSlice,
  [],
  [],
  InterviewSlice
> = (set, get) => ({
  ...initialState,
  InterviewMessageHandler: (client: Client, response: any) => {
    switch (response.type) {
      case INTERVIEW_MESSAGE_TYPE.USER_EXITED:
        userExitedHandler(client, response.data);
        break;
      case INTERVIEW_MESSAGE_TYPE.INTERVIEW_STARTED:
        interviewStartedHandler(client, set, response.data);
        break;
      case INTERVIEW_MESSAGE_TYPE.INTERVIEW_ENDED:
        interviewEndedHandler(client, set, response.data);
        break;
      case INTERVIEW_MESSAGE_TYPE.ANSWER_RECORDING_START:
        answerRecordingStartHandler(client, set, response.data);
        break;
      case INTERVIEW_MESSAGE_TYPE.STT_RESULT:
        sttResultHandler(client, get, response.data, response.timestamp);
        break;
      case INTERVIEW_MESSAGE_TYPE.NEXT_QUESTION_SELECTED:
        nextQuestionSelectedHandler(client, set, response.data);
        break;
      case INTERVIEW_MESSAGE_TYPE.CUSTOM_QUESTION_START:
        customQuestionStartHandler(client, set, response.data);
        break;
      case INTERVIEW_MESSAGE_TYPE.CUSTOM_QUESTION_CREATED:
        customQuestionCreatedHandler(client, set, response.data);
        break;
      case INTERVIEW_MESSAGE_TYPE.CHAT:
        chatHandler(client, get, response.data);
        break;
      default:
        errorHandler(client, response.data);
        break;
    }
  },
});

const userExitedHandler = (client: Client, data: any) => {
  console.log(data);
};

const interviewStartedHandler = (client: Client, set: any, data: any) => {
  console.log(data);
  const firstQuestion = data.firstQuestion;

  useInterviewQuestionStore.getState().setCurrentQuestion({
    question: firstQuestion.questionText,
    id: firstQuestion.questionId,
    questionType: firstQuestion.questionType,
  });
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.QUESTION_PRESENTED);
};

const interviewEndedHandler = (client: Client, set: any, data: any) => {
  console.log(data);
  useInterviewRoomStore.getState().setStatus(interviewStatus.END_INTERVIEW);
};

const answerRecordingStartHandler = (client: Client, set: any, data: any) => {
  useInterviewRoomStore.getState().setStatus(interviewStatus.ANSWER_START);
};

const sttResultHandler = (
  client: Client,
  get: any,
  data: any,
  timestamp: number,
) => {
  const message = {
    type: 'answer',
    sender: '답변',
    content: data.transcribedText,
    timestamp: timestamp,
    isMyMessage: false,
    confirmed: true,
  };
  get().addMessage(message);
};

const nextQuestionSelectedHandler = (client: Client, set: any, data: any) => {
  useInterviewQuestionStore.getState().setCurrentQuestion({
    question: data.questionText,
    id: data.questionId,
    questionType: data.questionType,
  });
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.QUESTION_PRESENTED);
};

const customQuestionStartHandler = (client: Client, set: any, data: any) => {
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.CUSTOM_QUESTION_START);
};

const customQuestionCreatedHandler = (client: Client, set: any, data: any) => {
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.CUSTOM_QUESTION_CREATED);
};

const chatHandler = (client: Client, get: any, data: any) => {
  const message = {
    type: 'chat',
    content: data.content,
    sender: data.senderNickname,
    timestamp: data.timestamp,
  };
  if (data.senderNickname === useUserStore.getState().nickname) {
    get().confirmMessage(message);
    return;
  }
  get().addMessage({ message });
};

const errorHandler = (client: Client, data: any) => {
  console.log(data);
};
