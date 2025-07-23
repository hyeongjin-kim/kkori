import { render, screen } from '@testing-library/react';
import MemoryRouterWrapped from '../common/MemoryRouterWrapped';
import CurrentQuestionBanner from './CurrentQuestionBanner';
import { CurrentQuestionBannerProps } from '../../types/CurrentQuestionBannerProps';

describe('CurrentQuestionBanner', () => {
  const mockQuestion: CurrentQuestionBannerProps = {
    id: 1,
    question: '현재 질문',
  };

  beforeEach(() => {
    render(
      <MemoryRouterWrapped
        component={
          <CurrentQuestionBanner
            id={mockQuestion.id}
            question={mockQuestion.question}
          />
        }
      />,
    );
  });

  test('CurrentQuestionBanner 컴포넌트가 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('current-question-banner'),
    ).toBeInTheDocument();
  });
});
