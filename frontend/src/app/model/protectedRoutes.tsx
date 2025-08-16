import PairPracticePage from '@/pages/pairPracticePage/page/index';
import SoloPracticePage from '@/pages/soloPracticePage/page/index';
import QuestionSetCreatePage from '@/pages/questionSetCreatePage/page/index';
import MyQuestionSetPage from '@/pages/myQuestionSetPage/page/index';
import QuestionSetUpdatePage from '@/pages/questionSetUpdatePage/page';
import { AppRoutes, Route } from '@/app/model/routes';
import MyInterviewResultPage from '@/pages/myInterviewResultPage/page';
import InterviewRecordDetailPage from '@/pages/InterviewDetailPage/page';

const protectedRoutes: readonly Route[] = [
  {
    path: '/pair-practice',
    element: <PairPracticePage />,
    label: 'pair-practice-page',
  },
  {
    path: '/solo-practice',
    element: <SoloPracticePage />,
    label: 'solo-practice-page',
  },
  {
    path: '/question-set-create',
    element: <QuestionSetCreatePage />,
    label: 'question-set-create-page',
  },
  {
    path: '/my-question-set',
    element: <MyQuestionSetPage />,
    label: 'my-question-set-page',
  },
  {
    path: '/question-set-update/:id',
    element: <QuestionSetUpdatePage />,
    label: 'question-set-update-page',
  },
  {
    path: '/my-interview-result',
    element: <MyInterviewResultPage />,
    label: 'my-interview-result-page',
  },
  {
    path: '/interview-record-detail/:interviewId',
    element: <InterviewRecordDetailPage />,
    label: 'interview-record-detail-page',
  },
];

export const protectedAppRoutes: AppRoutes = Object.freeze({
  protected: protectedRoutes,
});
