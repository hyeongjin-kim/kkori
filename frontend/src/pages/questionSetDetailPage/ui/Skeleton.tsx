import SkeletonLine from '@/shared/ui/SkeletonLine';

function Skeleton() {
  return (
    <>
      <SkeletonLine w="w-2/3" />
      <SkeletonLine w="w-1/3" />
      <div className="mt-1 flex flex-wrap gap-2">
        <div className="h-6 w-16 animate-pulse rounded-full bg-gray-200" />
        <div className="h-6 w-20 animate-pulse rounded-full bg-gray-200" />
      </div>
    </>
  );
}

export default Skeleton;
