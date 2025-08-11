interface TagDisplayProps {
  tags: string[];
  onClick?: (tag: string) => void;
}

function TagDisplay({ tags, onClick }: TagDisplayProps) {
  return (
    <ul className="flex flex-wrap gap-2">
      {tags.map(tag => (
        <li
          key={tag}
          aria-label={`tag-filter-${tag}`}
          className="cursor-pointer rounded-md border border-gray-100 bg-gray-50 px-3 py-1 text-sm font-medium text-gray-500 shadow-sm transition-colors hover:bg-gray-200"
          onClick={() => onClick?.(tag)}
        >
          #{tag}
        </li>
      ))}
    </ul>
  );
}

export default TagDisplay;
