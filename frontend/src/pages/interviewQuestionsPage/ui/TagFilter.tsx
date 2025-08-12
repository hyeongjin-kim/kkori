interface TagFilterProps {
  tag: string;
  selected?: boolean;
  onClick?: (tag: string) => void;
}

export const tagFilterStyle = {
  selected:
    'bg-point-400 text-white hover:bg-white hover:text-point border border-point',
  unselected:
    'bg-white text-point hover:bg-point-400 hover:text-white border border-point',
};

function TagFilter({ tag, selected = false, onClick }: TagFilterProps) {
  return (
    <li key={tag}>
      <button
        aria-label={`tag-filter-${tag}`}
        onClick={() => onClick?.(tag)}
        className={`rounded-md px-3 py-1 text-sm font-medium transition-colors ${
          selected ? tagFilterStyle.selected : tagFilterStyle.unselected
        }`}
      >
        {tag}
      </button>
    </li>
  );
}

export default TagFilter;
