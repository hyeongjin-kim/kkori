import { useNavigate } from "react-router-dom";

interface PracticeButtonProps {
  text: string;
  path: string;
}

function PracticeButton({ text, path }: PracticeButtonProps) {
  const navigate = useNavigate();
  return <button onClick={() => navigate(path)}>{text}</button>;
}

export default PracticeButton;