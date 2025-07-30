import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '@components/common/MemoryRouterWrapped';
import CurrentQuestionDisplay from '@components/practicePage/CurrentQuestionDisplay';
import { CurrentQuestionDisplayProps } from '@/customTypes/practicePage/CurrentQuestionDisplayProps';
import { mockupQuestion } from '@/__mocks__/questionMocks';

describe('CurrentQuestionDisplay', () => {
  beforeEach(() => {
    render(
      <MemoryRouterWrapped
        component={
          <CurrentQuestionDisplay
            id={mockupQuestion.id}
            question={mockupQuestion.question}
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
});
