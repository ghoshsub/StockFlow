import { ErrorBoundary } from '@/shared/components/ErrorBoundary';
import { AppRouter } from '@/routes/AppRouter';

function App() {
  return (
    <ErrorBoundary>
      <AppRouter />
    </ErrorBoundary>
  );
}

export default App;
