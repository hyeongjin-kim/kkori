import { useNavigate } from 'react-router-dom';

function SoloPracticeButton() {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate('/solo-practice');
  };
  return <button onClick={handleClick}>혼자 연습하기</button>;
}

export default SoloPracticeButton;
