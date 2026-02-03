import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchPersonByUserId(userId) {
	const response = await api.get(`/persons/by-user/${userId}`);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function usePersonByUserId(userId) {
	return useQuery({
		queryKey: ["person-by-user-id", userId],
		queryFn: () => fetchPersonByUserId(userId),
	});
}
