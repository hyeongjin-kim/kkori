import { QuestionSetResponse } from '@/entities/questionSet/model/response';

interface QuestionSetTagListProps {
  questionSet: QuestionSetResponse;
}

function QuestionSetTagList({ questionSet }: QuestionSetTagListProps) {
  const { tags } = questionSet;
  return tags?.length ? (
    <ul aria-label="question-set-tag-list" className="flex flex-wrap gap-2">
      {tags.slice(0, 3).map(({ tag }) => (
        <li
          key={tag}
          className="rounded-full bg-gray-50 px-2.5 py-1 text-xs font-medium text-gray-600 ring-1 ring-gray-200 transition group-hover:bg-gray-100"
        >
          #{tag}
        </li>
      ))}

      {tags.length > 3 && (
        <li
          className="rounded-full bg-gray-50 px-2.5 py-1 text-xs font-semibold text-gray-700 ring-1 ring-gray-200"
          title={`추가 태그: ${tags
            .slice(3)
            .map(t => t.tag)
            .join(', ')}`}
        >
          +{tags.length - 3}
          <span className="sr-only">개의 추가 태그</span>
        </li>
      )}
    </ul>
  ) : null;
}

export default QuestionSetTagList;
