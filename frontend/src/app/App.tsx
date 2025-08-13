import '@/app/styles/App.css';
import { BrowserRouter as Router } from 'react-router-dom';
import AppRouter from '@/app/routes/AppRouter';
import { QueryProvider } from '@/app/providers/QueryProvider';
import MeBootstrap from '@/app/bootstrap/MeBootstrap';
import { Toaster } from 'sonner';

function App() {
  return (
    <QueryProvider>
      <Router>
        <Toaster
          position="top-right"
          richColors
          closeButton
          expand
          style={{ zIndex: 9999, top: '50px' }}
        />
        <MeBootstrap />
        <AppRouter />
      </Router>
    </QueryProvider>
  );
}

export default App;
