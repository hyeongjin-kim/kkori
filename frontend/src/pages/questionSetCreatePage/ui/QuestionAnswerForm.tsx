import { useState } from 'react';
import LabeledTextAreaField from '@/shared/ui/LabeledTextAreaField';

interface QuestionAnswer {
  question: string;
  answer: string;
}

const initialQuestionAnswer: QuestionAnswer = {
  question: '',
  answer: '',
};

function QuestionAnswerForm() {
  const [questionAnswerList, setQuestionAnswerList] = useState<
    QuestionAnswer[]
  >([initialQuestionAnswer]);

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
    setQuestionAnswerList(prev => [...prev, { question: '', answer: '' }]);
  };

  return (
    <section
      aria-label="question-answer-form"
      className="w-2/3 min-w-[500px] rounded-xl border border-gray-200 bg-white p-8 shadow-sm"
    >
      <h3 className="mb-6 text-xl font-bold text-gray-900">질문 답변 생성</h3>
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
              label="질문"
              placeholder="질문을 입력하세요"
              value={qa.question}
              onChange={e => handleQuestionChange(index, e.target.value)}
            />

            <LabeledTextAreaField
              displayTitle="답변"
              label="답변"
              placeholder="답변을 입력하세요"
              value={qa.answer}
              onChange={e => handleAnswerChange(index, e.target.value)}
            />
          </div>
        </div>
      ))}

      <button
        type="button"
        onClick={addNewQuestionAnswer}
        className="ml-auto block items-center gap-2 rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-50 active:scale-[0.99]"
      >
        <span aria-hidden>➕</span>
        질문 추가하기
      </button>

      <button
        type="button"
        className="inline-flex items-center justify-center rounded-lg bg-blue-600 px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 active:scale-[0.99]"
      >
        질문 세트 생성하기
      </button>
    </section>
  );
}

export default QuestionAnswerForm;
