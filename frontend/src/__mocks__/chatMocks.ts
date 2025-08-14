import {
  CHAT_TYPES,
  Message,
} from '@/widgets/chattingWindow/model/chattingWindowType';

export const mockMessageExamples: Message[] = [
  {
    type: CHAT_TYPES.question,
    sender: '질문',
    content: '명령형 프로그래밍과 선언형 프로그래밍이 무엇인가요?',
    timestamp: new Date().getTime(),
    confirmed: true,
  },
  {
    type: CHAT_TYPES.answer,
    sender: '답변',
    content: `명령형 프로그래밍은 컴퓨터에게 어떻게 동작할 지 명령하는 절차 중심의 프로그래밍 방식입니다. 선언형 프로그래밍은 무엇을 얻고 싶은지를 선언하는 결과 중심의 프로그래밍 방식입니다.`,
    timestamp: new Date().getTime(),
    confirmed: true,
  },
  {
    type: CHAT_TYPES.chat,
    sender: '면접관',
    content: '두 가지 개념을 잘 나눠서 설명하셨네요. 좋습니다.',
    timestamp: new Date().getTime(),
    confirmed: true,
  },
  {
    type: CHAT_TYPES.chat,
    sender: '면접자',
    content: '감사합니다. 그러면 다음 꼬리 질문으로 넘어가겠습니다.',
    timestamp: new Date().getTime(),
    confirmed: true,
  },
];
