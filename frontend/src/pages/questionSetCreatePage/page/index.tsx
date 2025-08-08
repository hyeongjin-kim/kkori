import QuestionSetForm from '@/pages/questionSetCreatePage/ui/QuestionSetForm';
import QuestionAnswerForm from '@/pages/questionSetCreatePage/ui/QuestionAnswerForm';
import { useState } from 'react';

interface QuestionAnswer {
  question: string;
  answer: string;
}

const initialQuestionAnswer: QuestionAnswer = {
  question: '',
  answer: '',
};

function QuestionSetCreatePage() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [isShared, setIsShared] = useState(false);
  const [tagList, setTagList] = useState<Set<string>>(new Set());
  const [questionAnswerList, setQuestionAnswerList] = useState<
    QuestionAnswer[]
  >([initialQuestionAnswer]);
  return (
    <main
      aria-label="question-set-create-page"
      className="bg-background relative flex h-full w-full flex-col items-center justify-around gap-8 px-30 py-8"
    >
      <QuestionSetForm
        title={title}
        description={description}
        isShared={isShared}
        tagList={tagList}
        onChange={{
          title: setTitle,
          description: setDescription,
          isShared: setIsShared,
          tagList: setTagList,
        }}
      />
      <QuestionAnswerForm
        questionAnswerList={questionAnswerList}
        setQuestionAnswerList={setQuestionAnswerList}
        onSubmit={() => {
          console.log(
            title,
            description,
            isShared,
            tagList,
            questionAnswerList,
          );
        }}
      />
    </main>
  );
}

export default QuestionSetCreatePage;
