import { Loading } from "@/components/loading";

export function LoadingPage() {
	return (
		<div className="flex h-screen items-center justify-center gap-12">
			<div className="flex flex-col items-center gap-6">
				<div className="group/tag w-fit overflow-hidden rounded border border-transparent bg-gradient-to-b from-gray-600 to-gray-600/30 bg-origin-border">
					<div className="flex w-fit items-center rounded bg-black p-2 lg:px-4">
						<Loading className="h-8" />
					</div>
				</div>
			</div>
		</div>
	);
}
