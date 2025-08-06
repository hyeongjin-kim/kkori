import { TAG_FILTER_LIST } from '@/pages/interviewQuestionsPage/model/constants';
import TagFilter from '@/pages/interviewQuestionsPage/ui/TagFilter';
import { Tag } from '@/entities/questionSet/model/type';

interface TagFilterListProps {
  selectedTag: Tag | null;
  onClick: (tag: Tag) => void;
}

function TagFilterList({ selectedTag, onClick }: TagFilterListProps) {
  return (
    <ul aria-label="tag-filter-list" className="flex gap-2">
      {TAG_FILTER_LIST.map(tag => (
        <li key={tag.id}>
          <TagFilter
            tag={tag}
            selected={selectedTag?.id === tag.id}
            onClick={() => onClick(tag)}
          />
        </li>
      ))}
    </ul>
  );
}

export default TagFilterList;
