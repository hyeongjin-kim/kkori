import '@/app/styles/App.css';
import { BrowserRouter as Router } from 'react-router-dom';
import AppRouter from '@/app/routes/AppRouter';
import { QueryProvider } from '@/app/providers/QueryProvider';
import MeBootstrap from '@/app/bootstrap/MeBootstrap';

function App() {
  return (
    <QueryProvider>
      <Router>
        <MeBootstrap />
        <AppRouter />
      </Router>
    </QueryProvider>
  );
}

export default App;
