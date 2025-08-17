import { useState } from 'react';
import Modal from '@/shared/ui/Modal';
import { useMyQuestionSets } from '@/entities/questionSet/model/useQuestionSetList';
import QuestionSetList from '@/pages/interviewQuestionsPage/ui/QuestionSetList';
import { useNavigate } from 'react-router-dom';
import GoToButton from '@/shared/ui/GoToButton';
import { usePracticeSessionStore } from '@/shared/lib/usePracticeSessionStore';
import { JOIN_ROOM_MODE } from '@/shared/lib/webSocketSlice';
import useInterviewRoomStore from '@/entities/interviewRoom/model/useInterviewRoomStore';
import { interviewType } from '@/entities/interviewRoom/model/useInterviewRoomStore';

interface SelectQuestionSetModalProps {
  onClose: () => void;
  contentRef: React.RefObject<HTMLDivElement | null>;
}

function SelectQuestionSetModal({
  onClose,
  contentRef,
}: SelectQuestionSetModalProps) {
  const { setQuestionSetId } = usePracticeSessionStore();
  const { data, isLoading } = useMyQuestionSets({});
  const practiceMode = useInterviewRoomStore(state => state.type);
  const setJoinRoomMode = usePracticeSessionStore(
    state => state.setJoinRoomMode,
  );
  const questionSets = data?.data.content ?? [];
  const navigate = useNavigate();
  const handleQuestionSetClick = (questionSetId: number) => {
    setQuestionSetId(questionSetId);
    onClose();
    setJoinRoomMode(JOIN_ROOM_MODE.CREATE_ROOM);
    navigate(
      practiceMode === interviewType.PAIR ? '/pair-practice' : '/solo-practice',
    );
  };

  return (
    <Modal
      title="면접 질문 세트를 선택하세요"
      subtitle="원하는 질문 세트를 골라보세요."
      onClose={onClose}
      contentRef={contentRef}
    >
      {questionSets.length > 0 ? (
        <QuestionSetList
          questionSets={questionSets}
          isLoading={isLoading}
          onClick={handleQuestionSetClick}
        />
      ) : (
        <div className="mx-auto flex h-full max-w-md flex-col items-center justify-center gap-5 px-6 py-12 text-center">
          <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-gray-50 ring-1 ring-gray-200">
            <span className="text-lg">🗂️</span>
          </div>

          <h2 className="text-xl font-semibold tracking-tight text-gray-900 md:text-2xl">
            아직 질문 세트가 없어요
          </h2>

          <p className="text-sm leading-6 text-gray-500">
            마음에 드는 면접 질문 세트를 찾아보거나
            <br className="hidden sm:block" />
            직접 만들어보세요.
          </p>

          <div className="mt-2 flex items-center gap-3">
            <GoToButton
              to="/question-set-create"
              label="question-set-create-button"
              text="새 세트 만들기"
              variant="primary"
            />
            <GoToButton
              to="/interview-questions"
              label="browse-question-sets-button"
              text="세트 둘러보기"
              variant="secondary"
            />
          </div>
        </div>
      )}
    </Modal>
  );
}

export default SelectQuestionSetModal;
