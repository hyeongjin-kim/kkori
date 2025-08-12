import { Suspense } from 'react';
import { QueryErrorResetBoundary } from '@tanstack/react-query';
import ErrorBoundary from '@/shared/ui/ErrorBoundary';

export function QueryBoundary({
  children,
  pendingFallback,
  errorFallback,
}: {
  children: React.ReactNode;
  pendingFallback: React.ReactNode;
  errorFallback: (reset: () => void, error: unknown) => React.ReactNode;
}) {
  return (
    <QueryErrorResetBoundary>
      {({ reset }) => (
        <ErrorBoundary
          fallbackRender={({ resetErrorBoundary, error }) =>
            errorFallback(() => {
              reset();
              resetErrorBoundary();
            }, error)
          }
        >
          <Suspense fallback={pendingFallback}>{children}</Suspense>
        </ErrorBoundary>
      )}
    </QueryErrorResetBoundary>
  );
}
