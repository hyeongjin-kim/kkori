import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./pages/homePage/index";
import SoloPracticePage from "./pages/soloPracticePage/index";
import PairPracticePage from "./pages/pairPracticePage/index";
import MainLayout from "./layouts/MainLayout";

function App() {
  return (
      <Router>
        <Routes>
            <Route element={<MainLayout />} >
            <Route path="/" element={<HomePage />} />
            <Route path="/solo-practice" element={<SoloPracticePage />} />
            <Route path="/pair-practice" element={<PairPracticePage />} />
          </Route>
        </Routes>
      </Router>
  );
}

export default App;
