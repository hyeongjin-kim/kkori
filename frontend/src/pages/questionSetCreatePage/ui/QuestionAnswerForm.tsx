import { useState } from 'react';
import LabeledTextAreaField from '@/shared/ui/LabeledTextAreaField';
import { QUESTION_ANSWER_FORMAT_TYPE } from '@/entities/questionSet/model/constants';

interface QuestionAnswer {
  question: string;
  answer: string;
}

const createInitialQuestionAnswer = (): QuestionAnswer => {
  return {
    question: '',
    answer: '',
  };
};

interface QuestionAnswerFormProps {
  questionAnswerList: QuestionAnswer[];
  setQuestionAnswerList: (questionAnswerList: QuestionAnswer[]) => void;
  onSubmit: () => void;
  type: (typeof QUESTION_ANSWER_FORMAT_TYPE)[keyof typeof QUESTION_ANSWER_FORMAT_TYPE];
}

function QuestionAnswerForm({
  questionAnswerList,
  setQuestionAnswerList,
  onSubmit,
  type,
}: QuestionAnswerFormProps) {
  const handleQuestionChange = (index: number, value: string) => {
    const newList = [...questionAnswerList];
    newList[index].question = value;
    setQuestionAnswerList(newList);
  };

  const handleAnswerChange = (index: number, value: string) => {
    const newList = [...questionAnswerList];
    newList[index].answer = value;
    setQuestionAnswerList(newList);
  };

  const addNewQuestionAnswer = () => {
    setQuestionAnswerList([
      ...questionAnswerList,
      createInitialQuestionAnswer(),
    ]);
  };

  return (
    <section
      aria-label="question-answer-form"
      className="w-2/3 min-w-[500px] rounded-xl border border-gray-200 bg-white p-8 shadow-sm"
    >
      <h3 className="mb-6 text-xl font-bold text-gray-900">
        {`질문 답변 ${type}하기`}
      </h3>
      {questionAnswerList.map((qa, index) => (
        <div
          key={index}
          className="mb-6 rounded-2xl border border-gray-100 p-6"
        >
          <h4 className="mb-4 text-sm font-medium text-gray-900">
            {index + 1}번째 질문 / 답변
          </h4>

          <div className="space-y-4">
            <LabeledTextAreaField
              displayTitle="질문"
              label="question-input"
              placeholder="질문을 입력하세요"
              value={qa.question}
              onChange={e => handleQuestionChange(index, e.target.value)}
            />

            <LabeledTextAreaField
              displayTitle="답변"
              label="answer-input"
              placeholder="답변을 입력하세요"
              value={qa.answer}
              onChange={e => handleAnswerChange(index, e.target.value)}
            />
          </div>
        </div>
      ))}
      <button
        type="button"
        aria-label="add-question-button"
        onClick={addNewQuestionAnswer}
        className="ml-auto block items-center gap-2 rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-50 active:scale-[0.99]"
      >
        <span aria-hidden>➕ 질문 추가하기</span>
      </button>

      <button
        type="button"
        aria-label="submit-button"
        className="m-auto mt-4 block gap-1.5 rounded-xl border border-blue-600 bg-white px-4 py-2 text-sm font-semibold text-blue-600 shadow-sm transition hover:bg-blue-50 focus-visible:ring-2 focus-visible:ring-blue-500/60 focus-visible:outline-none active:scale-[0.99] disabled:cursor-not-allowed disabled:opacity-60"
        onClick={onSubmit}
      >
        {`질문 세트 ${type}하기`}
      </button>
    </section>
  );
}

export default QuestionAnswerForm;
