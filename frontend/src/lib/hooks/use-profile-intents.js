import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchProfileIntents(pageIndex, pageSize) {
	const response = await api.get(
		`/profiling/profile-intents?page=${pageIndex}&size=${pageSize}`,
	);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function useProfileIntents(pageIndex, pageSize) {
	return useQuery({
		queryKey: ["profile-intents", pageIndex, pageSize],
		queryFn: () => fetchProfileIntents(pageIndex, pageSize),
	});
}
