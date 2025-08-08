import { render, screen } from '@testing-library/react';
import QuestionAnswerForm from '@/pages/questionSetCreatePage/ui/QuestionAnswerForm';
import userEvent from '@testing-library/user-event';

describe('QuestionAnswerForm', () => {
  const setQuestionAnswerList = jest.fn();
  const onSubmit = jest.fn();
  let questionAnswerList = [{ question: 'test', answer: 'test' }];
  beforeEach(() => {
    jest.clearAllMocks();
    questionAnswerList = [{ question: 'test', answer: 'test' }];
    render(
      <QuestionAnswerForm
        questionAnswerList={questionAnswerList}
        setQuestionAnswerList={setQuestionAnswerList}
        onSubmit={onSubmit}
      />,
    );
  });

  test('QuestionAnswerForm이 렌더링 된다.', async () => {
    expect(screen.getByLabelText('question-answer-form')).toBeInTheDocument();
    expect(screen.getByLabelText('question-input')).toBeInTheDocument();
    expect(screen.getByLabelText('answer-input')).toBeInTheDocument();
    expect(screen.getByLabelText('add-question-button')).toBeInTheDocument();
    expect(screen.getByLabelText('submit-button')).toBeInTheDocument();
  });

  test('추가 버튼 클릭 시 새 항목이 추가된다', async () => {
    const addButton = screen.getByLabelText('add-question-button');
    await userEvent.click(addButton);

    expect(setQuestionAnswerList).toHaveBeenCalled(); // 호출 여부
    const lastArg = setQuestionAnswerList.mock.calls.at(-1)[0];
    expect(Array.isArray(lastArg)).toBe(true);
    expect(lastArg).toHaveLength(2);
    expect(lastArg[1]).toEqual({ question: '', answer: '' }); // 새 항목 형태 검증
  });

  test('QuestionAnswerForm의 값이 변경되면 onSubmit 함수가 호출된다.', async () => {
    const submitButton = screen.getByLabelText('submit-button');
    await userEvent.click(submitButton);
    expect(onSubmit).toHaveBeenCalled();
  });
});
