import { twMerge } from "tailwind-merge";
import { Spinner } from "./ui/spinner";

export function Loading({ className }) {
	return (
		<Spinner className={twMerge("mx-auto my-auto animate-spin", className)} />
	);
}
