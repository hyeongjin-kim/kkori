import { render, screen } from '@testing-library/react';
import QuestionSetForm from '@/pages/questionSetCreatePage/ui/QuestionSetForm';
import MemoryRouterWrapped from '@/app/routes/MemoryRouterWrapped';

describe('QuestionSetForm', () => {
  const title = 'test';
  const description = 'test';
  const isPublic = false;
  const tagList = new Set(['test']);

  test('QuestionSetForm이 렌더링 된다.', () => {
    render(
      <MemoryRouterWrapped
        component={
          <QuestionSetForm
            title={title}
            description={description}
            isPublic={isPublic}
            tagList={tagList}
            onChange={{
              title: () => {},
              description: () => {},
              isPublic: () => {},
              tagList: () => {},
            }}
          />
        }
      />,
    );
    expect(
      screen.getByRole('region', { name: 'question-set-form' }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole('heading', { name: '질문 세트 설정' }),
    ).toBeInTheDocument();
    expect(screen.getByLabelText('title-input')).toBeInTheDocument();
    expect(screen.getByLabelText('description-input')).toBeInTheDocument();
    expect(screen.getByLabelText('shared-toggle-switch')).toBeInTheDocument();
    expect(screen.getByLabelText('tag-input')).toBeInTheDocument();
  });
});
