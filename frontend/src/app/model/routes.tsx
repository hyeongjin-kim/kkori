import LoginPage from '@/pages/loginPage/page/index';
import MyPage from '@/pages/myPage/page/index';
import PairPracticePage from '@/pages/pairPracticePage/page/index';
import SoloPracticePage from '@/pages/soloPracticePage/page/index';
import InterviewQuestionsPage from '@/pages/interviewQuestionsPage/page/index';
import HomePage from '@/pages/homePage/page/index';
import QuestionSetCreatePage from '@/pages/questionSetCreatePage/page/index';
import QuestionSetDetailPage from '@/pages/questionSetDetailPage/page/index';
import MyQuestionSetPage from '@/pages/myQuestionSetPage/page/index';
interface Route {
  path: string; // TODO: 타입 정의 필요
  element: React.ReactNode;
  label: string;
}

interface AppRoutes {
  [layoutName: string]: readonly Route[];
}

const mainLayoutRoutes: readonly Route[] = [
  { path: '/', element: <HomePage />, label: 'home-page' },
  { path: '/login', element: <LoginPage />, label: 'login-page' },
  { path: '/my-page', element: <MyPage />, label: 'my-page' },
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
    path: '/interview-questions',
    element: <InterviewQuestionsPage />,
    label: 'interview-questions-page',
  },
  {
    path: '/question-set-create',
    element: <QuestionSetCreatePage />,
    label: 'question-set-create-page',
  },
  {
    path: '/question-set-detail/:id',
    element: <QuestionSetDetailPage />,
    label: 'question-set-detail-page',
  },
  {
    path: '/my-question-set',
    element: <MyQuestionSetPage />,
    label: 'my-question-set-page',
  },
];

const appRoutes: AppRoutes = Object.freeze({ mainLayout: mainLayoutRoutes });

export { appRoutes, mainLayoutRoutes };
