import {
  switchScreen,
  startAnswer,
  endAnswer,
  startInterview,
  endInterview,
} from '@/widgets/interviewSection/model';

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
    status: 'beforeInterview',
  },
  {
    onClick: startInterview,
    label: 'next-question-select',
    text: '다음 질문 선택',
    status: 'answerEnd',
  },
  {
    onClick: startAnswer,
    label: 'answer-start',
    text: '답변 시작',
    status: 'questionPresented',
  },
  {
    onClick: endAnswer,
    label: 'answer-end',
    text: '답변 종료',
    status: 'answerStart',
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '면접 종료',
    status: 'always',
  },
];

export const peerIntervieweeControlButtonProps = [
  {
    onClick: startInterview,
    label: 'interview-start',
    text: '면접 시작',
    status: 'beforeInterview',
  },
  {
    onClick: startInterview,
    label: 'next-question-select',
    text: '다음 질문 선택',
    status: 'answerEnd',
  },
  {
    onClick: startAnswer,
    label: 'answer-start',
    text: '답변 시작',
    status: 'answerEnd',
  },
  {
    onClick: endAnswer,
    label: 'answer-end',
    text: '답변 종료',
    status: 'questionPresented',
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '면접 종료',
    status: 'always',
  },
];

export const peerInterviewerControlButtonProps = [
  {
    onClick: switchScreen,
    label: 'screen-change',
    text: '화면 전환',
    status: 'beforeInterview',
  },
  {
    onClick: startInterview,
    label: 'interview-start',
    text: '면접 시작',
    status: 'beforeInterview',
  },
  {
    onClick: endInterview,
    label: 'interview-end',
    text: '면접 종료',
    status: 'always',
  },
  {
    onClick: switchScreen,
    label: 'screen-change',
    text: '화면 전환',
    status: 'always',
  },
  {
    onClick: startInterview,
    label: 'next-question-select',
    text: '다음 질문 선택',
    status: 'questionPresented',
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
