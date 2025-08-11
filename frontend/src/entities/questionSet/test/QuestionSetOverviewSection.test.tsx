import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import QuestionSetOverviewSection from '@/entities/questionSet/ui/QuestionSetOverviewSection';

jest.mock('@/entities/questionSet/model/useQuestionSetList', () => ({
  useQuestionSet: jest.fn(),
}));

import { useQuestionSet } from '@/entities/questionSet/model/useQuestionSetList';

const mockData = {
  data: {
    questionSetId: 1,
    title: '질문 세트 제목',
    description: '설명입니다',
    versionNumber: 3,
    parentVersionId: 1,
    isPublic: true,
    ownerNickname: '이찬',
    tags: ['자바', '백엔드'],
    createdAt: '2025-01-01T00:00:00',
    updatedAt: '2025-01-02T00:00:00',
  },
};

describe('QuestionSetOverviewSection', () => {
  beforeEach(() => {
    (useQuestionSet as jest.Mock).mockReturnValue({
      data: mockData,
      isLoading: false,
      isError: false,
    });
  });

  const renderWithRouter = (initial = '/question-set/1') =>
    render(
      <MemoryRouter initialEntries={[initial]}>
        <Routes>
          <Route
            path="/question-set/:id"
            element={<QuestionSetOverviewSection />}
          />
        </Routes>
      </MemoryRouter>,
    );

  it('렌더링되고 제목/배지/목록 버튼이 보인다', () => {
    renderWithRouter();

    expect(
      screen.getByRole('heading', { name: /질문 세트 제목/i }),
    ).toBeInTheDocument();
    expect(screen.getByText(/v3 \(forked from 1\)/i)).toBeInTheDocument();
    expect(screen.getByText('공개')).toBeInTheDocument();
    expect(screen.getByText(/작성자 : 이찬/)).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /목록으로/i })).toBeInTheDocument();
  });

  it('URL 파라미터가 달라도 훅이 다시 호출된다', () => {
    renderWithRouter('/question-set/42');
    expect(useQuestionSet).toHaveBeenCalledWith(42);
  });
});
