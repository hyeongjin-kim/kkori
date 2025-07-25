import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';
import CurrentQuestionDisplay from '@components/practicePage/CurrentQuestionDisplay';
import { CurrentQuestionDisplayProps } from '@customTypes/CurrentQuestionDisplayProps';

describe('CurrentQuestionDisplay', () => {
  const mockQuestion: CurrentQuestionDisplayProps = {
    id: 1,
    question: '현재 질문',
  };

  beforeEach(() => {
    render(
      <MemoryRouterWrapped
        component={
          <CurrentQuestionDisplay
            id={mockQuestion.id}
            question={mockQuestion.question}
          />
        }
      />,
    );
  });

  test('CurrentQuestionDisplay 컴포넌트가 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('current-question-display'),
    ).toBeInTheDocument();
  });

  test('question 텍스트가 렌더링 된다.', () => {
    expect(screen.getByText(mockQuestion.question)).toBeInTheDocument();
  });
});
