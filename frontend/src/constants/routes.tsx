import LoginPage from '@pages/loginPage/index';
import MyPage from '@pages/myPage/index';
import PairPracticePage from '@pages/pairPracticePage/index';
import SoloPracticePage from '@pages/soloPracticePage/index';
import InterviewQuestionsPage from '@pages/interviewQuestionsPage/index';
import HomePage from '@pages/homePage/index';
import PracticePage from '@pages/PracticePage/index';
interface Route {
  path: string;
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
    path: '/practice',
    element: <PracticePage />,
    label: 'practice-page',
  },
];

const appRoutes: AppRoutes = Object.freeze({ mainLayout: mainLayoutRoutes });

export { appRoutes, mainLayoutRoutes };
