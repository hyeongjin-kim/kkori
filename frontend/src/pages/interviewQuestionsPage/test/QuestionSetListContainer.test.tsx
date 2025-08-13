import { render, screen } from '@testing-library/react';
import QuestionSetListContainer from '@/pages/interviewQuestionsPage/ui/QuestionSetListContainer';
import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';
import { questionSetList } from '@/entities/questionSet/model/mock';

let selectedTag: string = TAG_FILTER_LIST[0].tag; // 기본: "전체" 같은 첫 항목
jest.mock(
  '@/pages/interviewQuestionsPage/model/useQuestionSetFilterStore',
  () => ({
    __esModule: true,
    default: (selector: any) => selector({ selectedTag }),
  }),
);

const useQuestionSetsMock = jest.fn();
jest.mock('@/entities/questionSet/model/useQuestionSetList', () => ({
  __esModule: true,
  useQuestionSets: (args: any) => useQuestionSetsMock(args),
}));

describe('QuestionSetListContainer', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    selectedTag = TAG_FILTER_LIST[0].tag;
  });

  test('에러면 에러 메시지를 렌더링한다', () => {
    useQuestionSetsMock.mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
    });

    render(<MemoryRouterWrapped component={<QuestionSetListContainer />} />);

    expect(screen.getByText('불러오기 실패')).toBeInTheDocument();
  });

  test('로딩이면 리스트 컴포넌트에 isLoading이 전달된다', () => {
    useQuestionSetsMock.mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    });

    render(<MemoryRouterWrapped component={<QuestionSetListContainer />} />);

    expect(
      screen.getByRole('list', { name: 'question-set-list' }),
    ).toBeInTheDocument();
  });

  test('성공이면 QuestionSetList가 렌더링된다', () => {
    useQuestionSetsMock.mockReturnValue({
      isLoading: false,
      isError: false,
      data: {
        data: { content: questionSetList },
      },
    });

    render(<MemoryRouterWrapped component={<QuestionSetListContainer />} />);

    expect(
      screen.getByRole('list', { name: 'question-set-list' }),
    ).toBeInTheDocument();
  });

  test('선택 태그가 첫 필터(예: 전체)이면 tags를 undefined로 호출한다', () => {
    useQuestionSetsMock.mockReturnValue({
      isLoading: false,
      isError: false,
      data: { data: { content: [] } },
    });

    render(<MemoryRouterWrapped component={<QuestionSetListContainer />} />);

    expect(useQuestionSetsMock).toHaveBeenCalledTimes(1);
    expect(useQuestionSetsMock).toHaveBeenCalledWith({ tags: undefined });
  });

  test('선택 태그가 일반 태그면 tags 배열로 호출한다', () => {
    selectedTag = '백엔드';
    useQuestionSetsMock.mockReturnValue({
      isLoading: false,
      isError: false,
      data: { data: { content: [] } },
    });

    render(<MemoryRouterWrapped component={<QuestionSetListContainer />} />);

    expect(useQuestionSetsMock).toHaveBeenCalledTimes(1);
    expect(useQuestionSetsMock).toHaveBeenCalledWith({ tags: ['백엔드'] });
  });
});
