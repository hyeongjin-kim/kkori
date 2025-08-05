import '@/app/styles/App.css';
import { BrowserRouter as Router } from 'react-router-dom';
import AppRouter from '@/app/routes/AppRouter';

function App() {
  return (
    <Router>
      <AppRouter />
    </Router>
  );
}

export default App;
