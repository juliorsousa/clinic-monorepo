export function NotFound() {
  return (
    <div className="flex h-screen items-center justify-center gap-12">
      <div className="flex flex-col items-center gap-6">
        <div className="group/tag w-fit overflow-hidden rounded border border-transparent bg-gradient-to-b from-gray-600 to-gray-600/30 bg-origin-border">
          <div className="flex max-h-8 w-fit items-center rounded bg-black p-2 lg:h-9 lg:px-4">
            <span className="whitespace-nowrap bg-gradient-to-r from-white to-white/40 bg-clip-text font-medium font-mono text-transparent text-xs lg:text-sm">
              404_page not found
            </span>
          </div>
        </div>
      </div>
    </div>
  )
}