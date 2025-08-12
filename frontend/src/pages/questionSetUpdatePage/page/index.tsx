import {
  useQuestionSet,
  useUpdateQuestionSet,
  useUpdateQuestionSetMetadata,
} from '@/entities/questionSet/model/useQuestionSetList';
import { QUESTION_ANSWER_FORMAT_TYPE } from '@/entities/questionSet/model/constants';
import { QuestionAnswer } from '@/pages/questionSetCreatePage/page';
import QuestionAnswerForm from '@/pages/questionSetCreatePage/ui/QuestionAnswerForm';
import QuestionSetForm from '@/pages/questionSetCreatePage/ui/QuestionSetForm';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

function QuestionSetUpdatePage() {
  const { id } = useParams();
  const questionSetId = Number(id);
  const { data, isLoading, isError } = useQuestionSet(questionSetId);
  const { mutate: updateQuestionSet } = useUpdateQuestionSet(questionSetId);
  const { mutate: updateQuestionSetMetadata } =
    useUpdateQuestionSetMetadata(questionSetId);
  const qs = data?.data;
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [isPublic, setIsPublic] = useState(false);
  const [tagList, setTagList] = useState<Set<string>>(new Set());
  const [questionAnswerList, setQuestionAnswerList] = useState<
    QuestionAnswer[]
  >([]);

  const handleMetaDataSubmit = () => {
    updateQuestionSetMetadata({
      title: title,
      description: description,
      isPublic: isPublic,
    });
  };

  const handleQuestionAnswerSubmit = () => {
    updateQuestionSet({
      questions: questionAnswerList.map((question, index) => ({
        content: question.question,
        questionType: 1,
        expectedAnswer: question.answer,
        displayOrder: index + 1,
      })),
    });
  };

  useEffect(() => {
    if (qs) {
      setTitle(qs.title);
      setDescription(qs.description);
      setIsPublic(qs.isPublic);
      setTagList(new Set(qs.tags.map(tag => tag.tag)));
      setQuestionAnswerList(
        qs.questionMaps.map(question => ({
          question: question.question.content,
          answer: question.answer.content,
        })),
      );
    }
  }, [qs]);

  return (
    <main
      aria-label="question-set-update-page"
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
        onSubmit={handleMetaDataSubmit}
      />
      <QuestionAnswerForm
        questionAnswerList={questionAnswerList}
        setQuestionAnswerList={setQuestionAnswerList}
        onSubmit={handleQuestionAnswerSubmit}
        type={QUESTION_ANSWER_FORMAT_TYPE.update}
      />
    </main>
  );
}

export default QuestionSetUpdatePage;
