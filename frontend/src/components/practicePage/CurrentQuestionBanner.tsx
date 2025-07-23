import { CurrentQuestionBannerProps } from '../../types/CurrentQuestionBannerProps';

function CurrentQuestionBanner({ id, question }: CurrentQuestionBannerProps) {
  return (
    <div
      aria-label="current-question-banner"
      className="text-center text-lg font-bold text-white"
      key={id}
    >
      {question}
    </div>
  );
}

export default CurrentQuestionBanner;
