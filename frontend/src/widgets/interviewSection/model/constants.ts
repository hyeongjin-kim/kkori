import {
  switchScreen,
  startAnswer,
  endAnswer,
  startInterview,
  endInterview,
  openNextQuestionModal,
  startCustomQuestion,
  endCustomQuestion,
  exitInterview,
  switchRole,
  copyRoomId,
} from '@/widgets/interviewSection/model';
import { controlStatus } from '@/widgets/interviewSection/model/types';

export const soloIntervieweeControlButtonProps = [
  {
    onClick: switchScreen,
    label: 'screen-change',
    text: '화면 전환',
    status: controlStatus.ALWAYS,
  },
  {
    onClick: startInterview,
    label: 'interview-start',
    text: '면접 시작',
    status: controlStatus.BEFORE_INTERVIEW,
  },
  {
    onClick: openNextQuestionModal,
    label: 'next-question-select',
    text: '다음 질문 선택',
    status: controlStatus.NEXT_QUESTION_PRESENTED,
  },
  {
    onClick: startAnswer,
    label: 'answer-start',
    text: '답변 녹음 시작',
    status: controlStatus.QUESTION_PRESENTED,
  },
  {
    onClick: endAnswer,
    label: 'answer-end',
    text: '답변 녹음 종료',
    status: controlStatus.ANSWER_START,
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '면접  종료',
    status: controlStatus.DURING_INTERVIEW,
  },
  {
    onClick: startCustomQuestion,
    label: 'custom-question-start',
    text: '커스텀 질문 녹음 시작',
    status: controlStatus.CUSTOM_QUESTION_SELECTED,
  },
  {
    onClick: endCustomQuestion,
    label: 'custom-question-end',
    text: '커스텀 질문 녹음 종료',
    status: controlStatus.CUSTOM_QUESTION_START,
  },
  {
    onClick: exitInterview,
    label: 'exit-interview',
    text: '나가기',
    status: controlStatus.END_INTERVIEW,
    path: '/',
  },
];

export const pairIntervieweeControlButtonProps = [
  {
    onClick: copyRoomId,
    label: 'copy-room-id',
    text: '방 코드 복사',
    status: controlStatus.BEFORE_INTERVIEW,
  },
  {
    onClick: switchScreen,
    label: 'screen-change',
    text: '화면 전환',
    status: controlStatus.ALWAYS,
  },
  {
    onClick: switchRole,
    label: 'role-change',
    text: '역할 변경',
    status: controlStatus.BEFORE_INTERVIEW,
  },
  {
    onClick: startInterview,
    label: 'interview-start',
    text: '면접 시작',
    status: controlStatus.BEFORE_INTERVIEW,
  },
  {
    onClick: startAnswer,
    label: 'answer-start',
    text: '답변 시작',
    status: controlStatus.QUESTION_PRESENTED,
  },
  {
    onClick: endAnswer,
    label: 'answer-end',
    text: '답변 종료',
    status: controlStatus.ANSWER_START,
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '면접 종료',
    status: controlStatus.DURING_INTERVIEW,
  },
  {
    onClick: exitInterview,
    label: 'exit-interview',
    text: '나가기',
    status: controlStatus.END_INTERVIEW,
    path: '/',
  },
];

export const pairInterviewerControlButtonProps = [
  {
    onClick: copyRoomId,
    label: 'copy-room-id',
    text: '방 코드 복사',
    status: controlStatus.BEFORE_INTERVIEW,
  },
  {
    onClick: switchScreen,
    label: 'screen-change',
    text: '화면 전환',
    status: controlStatus.ALWAYS,
  },
  {
    onClick: switchRole,
    label: 'role-change',
    text: '역할 변경',
    status: controlStatus.BEFORE_INTERVIEW,
  },
  {
    onClick: openNextQuestionModal,
    label: 'next-question-select',
    text: '다음 질문 선택',
    status: controlStatus.NEXT_QUESTION_PRESENTED,
  },
  {
    onClick: startCustomQuestion,
    label: 'custom-question-start',
    text: '커스텀 질문 녹음 시작',
    status: controlStatus.CUSTOM_QUESTION_SELECTED,
  },
  {
    onClick: endCustomQuestion,
    label: 'custom-question-end',
    text: '커스텀 질문 녹음 종료',
    status: controlStatus.CUSTOM_QUESTION_START,
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '면접 종료',
    status: controlStatus.DURING_INTERVIEW,
  },
  {
    onClick: exitInterview,
    label: 'exit-interview',
    text: '나가기',
    status: controlStatus.END_INTERVIEW,
    path: '/',
  },
];

export const INTERVIEW_MESSAGE_TYPE = {
  USER_EXITED: 'user-exited',
  ROLES_SWAP: 'roles-swapped',
  INTERVIEW_STARTED: 'interview-started',
  INTERVIEW_ENDED: 'interview-ended',
  ANSWER_RECORDING_START: 'answer-recording-start',
  STT_RESULT: 'stt-result',
  NEXT_QUESTION_SELECTED: 'next-question-selected',
  CUSTOM_QUESTION_START: 'custom-question-start',
  CUSTOM_QUESTION_CREATED: 'custom-question-created',
  CHAT: 'chat',
};
