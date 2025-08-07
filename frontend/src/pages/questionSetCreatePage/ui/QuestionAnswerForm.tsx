import { useState } from 'react';
import LabeledTextField from '@/shared/ui/LabeledTextField';

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
      className="w-2/3 rounded-xl border border-gray-200 bg-white p-8 shadow-sm"
    >
      <h3 className="mb-6 text-xl font-bold text-gray-900">질문 답변 생성</h3>
      {questionAnswerList.map((qa, index) => (
        <div
          key={index}
          className="mb-6 rounded-2xl border border-gray-100 bg-gray-50 p-6 shadow-inner"
        >
          <h4 className="mb-4 text-sm font-medium text-gray-500">
            질문 {index + 1}
          </h4>

          <div className="space-y-4">
            <LabeledTextField
              displayTitle="질문"
              label="질문"
              placeholder="질문을 입력하세요"
              value={qa.question}
              onChange={e => handleQuestionChange(index, e.target.value)}
            />

            <LabeledTextField
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
        className="rounded-md bg-blue-500 px-4 py-2 text-white"
      >
        질문 추가하기
      </button>
    </section>
  );
}

export default QuestionAnswerForm;
