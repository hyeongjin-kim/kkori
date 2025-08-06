function TagInputForm() {
  return (
    <form aria-label="tag-input-form" className="flex w-full gap-2">
      <input
        type="text"
        placeholder="태그를 입력해주세요."
        className="border-point-400 text-point flex-1 rounded-md border p-2 focus:outline-none"
      />
      <button
        type="submit"
        className="bg-point-400 rounded-md px-4 py-2 text-white"
      >
        조회
      </button>
    </form>
  );
}

export default TagInputForm;
