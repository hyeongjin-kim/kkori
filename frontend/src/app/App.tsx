import '@/app/styles/App.css';
import { BrowserRouter as Router } from 'react-router-dom';
import AppRouter from '@/app/routes/AppRouter';
import { QueryProvider } from '@/app/providers/QueryProvider';
import NextQuestionModal from '@/widgets/interviewSection/ui/NextQuestionModal';

function App() {
  return (
    <QueryProvider>
      <Router>
        <AppRouter />
      </Router>
      <NextQuestionModal />
    </QueryProvider>
  );
}

export default App;
