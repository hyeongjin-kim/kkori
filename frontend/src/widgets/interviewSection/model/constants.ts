import {
  switchScreen,
  startAnswer,
  endAnswer,
  startInterview,
  endInterview,
} from '@/widgets/interviewSection/model';

export const interviewControlButtonProps = [
  {
    onClick: switchScreen,
    label: 'screen-change',
    text: '화면 전환',
  },
  {
    onClick: startAnswer,
    label: 'answer-start',
    text: '답변 시작',
  },
  {
    onClick: endAnswer,
    label: 'answer-end',
    text: '답변 종료',
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '나가기',
  },
];

export const preInterviewControlButtonProps = [
  {
    onClick: startInterview,
    label: 'interview-start',
    text: '면접 시작',
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '나가기',
  },
];

export const INTERVIEW_MESSAGE_TYPE = {
  USER_EXITED: 'user-exited',
  INTERVIEW_STARTED: 'interview-started',
  INTERVIEW_ENDED: 'interview-ended',
  ANSWER_RECORDING_START: 'answer-recording-start',
  STT_RESULT: 'stt-result',
  NEXT_QUESTION_CHOICE: 'next-question-choice',
  NEXT_QUESTION_SELECTED: 'next-question-selected',
  CUSTOM_QUESTION_START: 'custom-question-start',
  CUSTOM_QUESTION_CREATED: 'custom-question-created',
  CHAT: 'chat',
};
