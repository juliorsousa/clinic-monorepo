import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchDoctor(id) {
	const response = await api.get(`/doctors/${id}`);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function useDoctor(id) {
	return useQuery({
		queryKey: ["doctor", id],
		queryFn: () => fetchDoctor(id),
	});
}
