import { Client } from '@stomp/stompjs';
import { StateCreator } from 'zustand';
import { INTERVIEW_MESSAGE_TYPE } from './constants';
import useInterviewRoomStore, {
  interviewStatus,
} from '@/entities/interviewRoom/model/useInterviewRoomStore';

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
        sttResultHandler(client, set, response.data);
        break;
      case INTERVIEW_MESSAGE_TYPE.NEXT_QUESTION_CHOICE:
        nextQuestionChoiceHandler(client, set, response.data);
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
        chatHandler(client, set, response.data);
        break;
      default:
        errorHandler(client, response.data);
        break;
    }
  },
});

const userExitedHandler = (client: Client, response: any) => {
  console.log(response);
};

const interviewStartedHandler = (client: Client, set: any, response: any) => {
  useInterviewRoomStore
    .getState()
    .setStatus(
      interviewStatus.QUESTION_PRESENTED as keyof typeof interviewStatus,
    );
};

const interviewEndedHandler = (client: Client, set: any, response: any) => {
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.END_INTERVIEW as keyof typeof interviewStatus);
};

const answerRecordingStartHandler = (
  client: Client,
  set: any,
  response: any,
) => {
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.ANSWER_START as keyof typeof interviewStatus);
};

const sttResultHandler = (client: Client, set: any, response: any) => {
  useInterviewRoomStore
    .getState()
    .setStatus(interviewStatus.ANSWER_END as keyof typeof interviewStatus);
};

const nextQuestionChoiceHandler = (client: Client, set: any, response: any) => {
  useInterviewRoomStore
    .getState()
    .setStatus(
      interviewStatus.NEXT_QUESTIONS_PRESENTED as keyof typeof interviewStatus,
    );
};

const nextQuestionSelectedHandler = (
  client: Client,
  set: any,
  response: any,
) => {
  useInterviewRoomStore
    .getState()
    .setStatus(
      interviewStatus.NEXT_QUESTION_SELECTED as keyof typeof interviewStatus,
    );
};

const customQuestionStartHandler = (
  client: Client,
  set: any,
  response: any,
) => {
  useInterviewRoomStore
    .getState()
    .setStatus(
      interviewStatus.CUSTOM_QUESTION_START as keyof typeof interviewStatus,
    );
};

const customQuestionCreatedHandler = (
  client: Client,
  set: any,
  response: any,
) => {
  useInterviewRoomStore
    .getState()
    .setStatus(
      interviewStatus.CUSTOM_QUESTION_CREATED as keyof typeof interviewStatus,
    );
};

const chatHandler = (client: Client, set: any, response: any) => {};

const errorHandler = (client: Client, response: any) => {
  console.log(response);
};
