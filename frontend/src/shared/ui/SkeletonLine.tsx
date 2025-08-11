export default function SkeletonLine({ w = 'w-full' }: { w?: string }) {
  return (
    <div role="line" className={`h-4 ${w} animate-pulse rounded bg-gray-200`} />
  );
}
