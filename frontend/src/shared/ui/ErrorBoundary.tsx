import React, { Component, ReactNode } from 'react';

type FallbackRender = (args: {
  error: unknown;
  resetErrorBoundary: () => void;
}) => ReactNode;

interface Props {
  children: ReactNode;
  fallbackRender: FallbackRender;
  onReset?: () => void;
  resetKeys?: Array<unknown>;
}

interface State {
  error: unknown | null;
}

export default class ErrorBoundary extends Component<Props, State> {
  state: State = { error: null };

  static getDerivedStateFromError(error: unknown): State {
    return { error };
  }

  componentDidCatch(error: unknown, info: unknown) {
    console.error(error, info);
  }

  componentDidUpdate(prevProps: Props) {
    const { resetKeys } = this.props;
    if (
      this.state.error &&
      resetKeys &&
      !shallowEqual(prevProps.resetKeys, resetKeys)
    ) {
      this.resetErrorBoundary();
    }
  }

  resetErrorBoundary = () => {
    this.setState({ error: null });
    this.props.onReset?.();
  };

  render() {
    const { error } = this.state;
    if (error) {
      return this.props.fallbackRender({
        error,
        resetErrorBoundary: this.resetErrorBoundary,
      });
    }
    return this.props.children;
  }
}

function shallowEqual(a?: Array<unknown>, b?: Array<unknown>) {
  if (a === b) return true;
  if (!a || !b || a.length !== b.length) return false;
  for (let i = 0; i < a.length; i++) if (a[i] !== b[i]) return false;
  return true;
}
