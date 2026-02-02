import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchPatient(id) {
	const response = await api.get(`/patients/${id}`);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function usePatient(id) {
	return useQuery({
		queryKey: ["patient", id],
		queryFn: () => fetchPatient(id),
	});
}
