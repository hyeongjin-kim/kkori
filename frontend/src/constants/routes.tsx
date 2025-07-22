import LoginPage from "../pages/loginPage/index";
import MyPage from "../pages/myPage/index";
import PairPracticePage from "../pages/pairPracticePage/index";
import SoloPracticePage from "../pages/soloPracticePage/index";
import InterviewQuestionsPage from "../pages/interviewQuestionsPage/index";
import HomePage from "../pages/homePage/index";
import { RouteObject } from "react-router-dom";

const mainLayoutRoutes: readonly RouteObject[] = Object.freeze([
    { path: "/", element: <HomePage /> },
    { path: "/login", element: <LoginPage /> },
    { path: "/my-page", element: <MyPage />},
    { path: "/pair-practice", element: <PairPracticePage />},
    { path: "/solo-practice", element: <SoloPracticePage />},
    { path: "/interview-questions", element: <InterviewQuestionsPage />},
]);

export { mainLayoutRoutes };
