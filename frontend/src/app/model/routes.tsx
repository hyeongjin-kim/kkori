import LoginPage from '@/pages/loginPage/page/index';
import InterviewQuestionsPage from '@/pages/interviewQuestionsPage/page/index';
import HomePage from '@/pages/homePage/page/index';
import QuestionSetDetailPage from '@/pages/questionSetDetailPage/page/index';

export interface Route {
  path: string;
  element: React.ReactNode;
  label: string;
}

export interface AppRoutes {
  [layoutName: string]: readonly Route[];
}

const mainLayoutRoutes: readonly Route[] = [
  { path: '/', element: <HomePage />, label: 'home-page' },
  { path: '/login', element: <LoginPage />, label: 'login-page' },
  {
    path: '/interview-questions',
    element: <InterviewQuestionsPage />,
    label: 'interview-questions-page',
  },
  {
    path: '/question-set-detail/:id',
    element: <QuestionSetDetailPage />,
    label: 'question-set-detail-page',
  },
];

const appRoutes: AppRoutes = Object.freeze({ mainLayout: mainLayoutRoutes });

export { appRoutes, mainLayoutRoutes };
