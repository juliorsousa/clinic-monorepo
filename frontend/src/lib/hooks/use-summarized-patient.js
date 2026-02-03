import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchSummarizedPatient(id) {
	const response = await api.get(`/patients/${id}/summary`);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function useSummarizedPatient(id) {
	return useQuery({
		queryKey: ["summarized-patient", id],
		queryFn: () => fetchSummarizedPatient(id),
	});
}
