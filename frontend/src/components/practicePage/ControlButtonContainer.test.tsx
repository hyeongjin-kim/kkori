import { render, screen } from '@testing-library/react';
import ControlButtonContainer from '@/components/practicePage/ControlButtonContainer';

describe('ControlButtonContainer', () => {
  beforeEach(() => {
    render(<ControlButtonContainer />);
  });

  test('ControlButtonContainer가 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('control-button-container'),
    ).toBeInTheDocument();
  });

  test('화면 전환 버튼이 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('control-button-container'),
    ).toBeInTheDocument();
  });

  test('답변 시작 버튼이 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('control-button-container'),
    ).toBeInTheDocument();
  });

  test('답변 종료 버튼이 렌더링 된다.', () => {
    expect(
      screen.getByLabelText('control-button-container'),
    ).toBeInTheDocument();
  });
});
