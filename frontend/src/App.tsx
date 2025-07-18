import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./pages/homePage/index";
import SoloPracticePage from "./pages/soloPracticePage/index";
import PairPracticePage from "./pages/pairPracticePage/index";
import MainLayout from "./layouts/MainLayout";
import InterviewQuestionsPage from "./pages/interviewQuestionsPage/index";
import MyPage from "./pages/myPage/index";
import LoginPage from "./pages/loginPage/index";

function App() {
  return (
      <Router>
        <Routes>
          <Route element={<MainLayout />} >
            <Route path="/" element={<HomePage />} />
            <Route path="/solo-practice" element={<SoloPracticePage />} />
            <Route path="/pair-practice" element={<PairPracticePage />} />
            <Route path="/interview-questions" element={<InterviewQuestionsPage />} />
            <Route path="/my-page" element={<MyPage />} />
            <Route path="/login" element={<LoginPage />} />
          </Route>
        </Routes>
      </Router>
  );
}

export default App;
