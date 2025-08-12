import {
  switchScreen,
  startAnswer,
  endAnswer,
  startInterview,
  endInterview,
  openNextQuestionModal,
  startCustomQuestion,
  endCustomQuestion,
} from '@/widgets/interviewSection/model';
import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';

export const soloIntervieweeControlButtonProps = [
  {
    onClick: switchScreen,
    label: 'screen-change',
    text: '화면 전환',
    status: 'always',
  },
  {
    onClick: startInterview,
    label: 'interview-start',
    text: '면접 시작',
    status: interviewStatus.BEFORE_INTERVIEW,
  },
  {
    onClick: openNextQuestionModal,
    label: 'next-question-select',
    text: '다음 질문 선택',
    status: interviewStatus.NEXT_QUESTION_PRESENTED,
  },
  {
    onClick: startAnswer,
    label: 'answer-start',
    text: '답변 녹음 시작',
    status: interviewStatus.QUESTION_PRESENTED,
  },
  {
    onClick: endAnswer,
    label: 'answer-end',
    text: '답변 녹음 종료',
    status: interviewStatus.ANSWER_START,
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '면접  종료',
    status: interviewStatus.ALWAYS,
  },
  {
    onClick: startCustomQuestion,
    label: 'custom-question-start',
    text: '커스텀 질문 녹음 시작',
    status: interviewStatus.CUSTOM_QUESTION_SELECTED,
  },
  {
    onClick: endCustomQuestion,
    label: 'custom-question-end',
    text: '커스텀 질문 녹음 종료',
    status: interviewStatus.CUSTOM_QUESTION_START,
  },
];

export const peerIntervieweeControlButtonProps = [
  {
    onClick: startInterview,
    label: 'interview-start',
    text: '면접 시작',
    status: interviewStatus.BEFORE_INTERVIEW,
  },
  {
    onClick: openNextQuestionModal,
    label: 'next-question-select',
    text: '다음 질문 선택',
    status: interviewStatus.NEXT_QUESTION_PRESENTED,
  },
  {
    onClick: startAnswer,
    label: 'answer-start',
    text: '답변 시작',
    status: interviewStatus.QUESTION_PRESENTED,
  },
  {
    onClick: endAnswer,
    label: 'answer-end',
    text: '답변 종료',
    status: interviewStatus.ANSWER_START,
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '면접 종료',
    status: interviewStatus.ALWAYS,
  },
];

export const peerInterviewerControlButtonProps = [
  {
    onClick: switchScreen,
    label: 'screen-change',
    text: '화면 전환',
    status: interviewStatus.ALWAYS,
  },
  {
    onClick: startInterview,
    label: 'interview-start',
    text: '면접 시작',
    status: interviewStatus.BEFORE_INTERVIEW,
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '면접 종료',
    status: interviewStatus.ALWAYS,
  },
  {
    onClick: openNextQuestionModal,
    label: 'next-question-select',
    text: '다음 질문 선택',
    status: interviewStatus.NEXT_QUESTION_PRESENTED,
  },
];

export const INTERVIEW_MESSAGE_TYPE = {
  USER_EXITED: 'user-exited',
  INTERVIEW_STARTED: 'interview-started',
  INTERVIEW_ENDED: 'interview-ended',
  ANSWER_RECORDING_START: 'answer-recording-start',
  STT_RESULT: 'stt-result',
  NEXT_QUESTION_SELECTED: 'next-question-selected',
  CUSTOM_QUESTION_START: 'custom-question-start',
  CUSTOM_QUESTION_CREATED: 'custom-question-created',
  CHAT: 'chat',
};
