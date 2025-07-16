import { useNavigate } from 'react-router-dom';

function PairPracticeButton() {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate('/pair-practice');
  };
  return <button onClick={handleClick}>같이 연습하기</button>;
}

export default PairPracticeButton;