import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchPatients(pageIndex, pageSize) {
	const response = await api.get(
		`/patients/?page=${pageIndex}&size=${pageSize}`,
	);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function usePatients(pageIndex, pageSize) {
	return useQuery({
		queryKey: ["patients", pageIndex, pageSize],
		queryFn: () => fetchPatients(pageIndex, pageSize),
	});
}
