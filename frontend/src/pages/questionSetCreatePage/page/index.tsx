import QuestionSetForm from '@/pages/questionSetCreatePage/ui/QuestionSetForm';
import QuestionAnswerForm from '@/pages/questionSetCreatePage/ui/QuestionAnswerForm';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCreateQuestionSet } from '@/entities/questionSet/model/useQuestionSetList';
import { toast } from 'sonner';

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
  const [isPublic, setIsPublic] = useState(false);
  const [tagList, setTagList] = useState<Set<string>>(new Set());
  const [questionAnswerList, setQuestionAnswerList] = useState<
    QuestionAnswer[]
  >([initialQuestionAnswer]);
  const navigate = useNavigate();
  const { mutate: createQuestionSet } = useCreateQuestionSet();
  const handleSubmit = () => {
    createQuestionSet({
      title,
      description,
      tags: Array.from(tagList),
      questions: questionAnswerList.map(questionAnswer => ({
        content: questionAnswer.question,
        expectedAnswer: questionAnswer.answer,
        questionType: 1,
      })),
    });
    toast.success('질문 세트가 생성되었습니다.');
    navigate('/interview-questions');
  };
  return (
    <main
      aria-label="question-set-create-page"
      className="bg-background relative flex h-full w-full flex-col items-center justify-around gap-8 px-30 py-8"
    >
      <QuestionSetForm
        title={title}
        description={description}
        isPublic={isPublic}
        tagList={tagList}
        onChange={{
          title: setTitle,
          description: setDescription,
          isPublic: setIsPublic,
          tagList: setTagList,
        }}
      />
      <QuestionAnswerForm
        questionAnswerList={questionAnswerList}
        setQuestionAnswerList={setQuestionAnswerList}
        onSubmit={handleSubmit}
      />
    </main>
  );
}

export default QuestionSetCreatePage;
