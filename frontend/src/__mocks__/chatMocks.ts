import { CHAT_TYPES } from '@/customTypes/practicePage/NameTaggedMessageProps';

export const mockMessageExamples = [
  {
    id: '1',
    type: CHAT_TYPES.QUESTION,
    sender: 'tester',
    message: 'test',
  },
  {
    id: '2',
    type: CHAT_TYPES.ANSWER,
    sender: 'tester2',
    message: 'test2',
  },
  {
    id: '3',
    type: CHAT_TYPES.OPPONENT,
    sender: 'tester3',
    message: 'test3',
  },
  {
    id: '4',
    type: CHAT_TYPES.USER,
    sender: 'tester4',
    message: 'test4',
  },
];
