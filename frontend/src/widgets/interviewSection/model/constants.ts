import {
  switchScreen,
  startAnswer,
  endAnswer,
  startInterview,
  endInterview,
  startCustomQuestion,
  endCustomQuestion,
  exitInterview,
  switchRole,
  copyRoomId,
} from '@/widgets/interviewSection/model';
import { interviewStatus } from '@/entities/interviewRoom/model/useInterviewRoomStore';

export type ControlButtonProps = {
  onClick: () => void;
  label: string;
  text: string;
  status: string;
  path?: string;
};

type Dependencies = {
  openModal: () => void;
};

export function soloIntervieweeControlButtonProps({
  openModal,
}: Dependencies): ControlButtonProps[] {
  return [
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
      onClick: openModal,
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
    {
      onClick: exitInterview,
      label: 'exit-interview',
      text: '나가기',
      status: interviewStatus.END_INTERVIEW,
      path: '/',
    },
  ];
}

export function pairIntervieweeControlButtonProps({
  openModal,
}: Dependencies): ControlButtonProps[] {
  return [
    {
      onClick: copyRoomId,
      label: 'copy-room-id',
      text: '방 코드 복사',
      status: interviewStatus.BEFORE_INTERVIEW,
    },
    {
      onClick: switchScreen,
      label: 'screen-change',
      text: '화면 전환',
      status: 'always',
    },
    {
      onClick: switchRole,
      label: 'role-change',
      text: '역할 변경',
      status: interviewStatus.BEFORE_INTERVIEW,
    },
    {
      onClick: startInterview,
      label: 'interview-start',
      text: '면접 시작',
      status: interviewStatus.BEFORE_INTERVIEW,
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
    {
      onClick: exitInterview,
      label: 'exit-interview',
      text: '나가기',
      status: interviewStatus.END_INTERVIEW,
      path: '/',
    },
  ];
}

export function pairInterviewerControlButtonProps({
  openModal,
}: Dependencies): ControlButtonProps[] {
  return [
    {
      onClick: copyRoomId,
      label: 'copy-room-id',
      text: '방 코드 복사',
      status: interviewStatus.BEFORE_INTERVIEW,
    },
    {
      onClick: switchScreen,
      label: 'screen-change',
      text: '화면 전환',
      status: interviewStatus.ALWAYS,
    },
    {
      onClick: switchRole,
      label: 'role-change',
      text: '역할 변경',
      status: interviewStatus.BEFORE_INTERVIEW,
    },
    {
      onClick: openModal,
      label: 'next-question-select',
      text: '다음 질문 선택',
      status: interviewStatus.NEXT_QUESTION_PRESENTED,
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
    {
      onClick: endInterview,
      label: 'interview-end',
      text: '면접 종료',
      status: interviewStatus.ALWAYS,
    },
    {
      onClick: exitInterview,
      label: 'exit-interview',
      text: '나가기',
      status: interviewStatus.END_INTERVIEW,
      path: '/',
    },
  ];
}

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
