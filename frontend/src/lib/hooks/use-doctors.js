import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchDoctors(pageIndex, pageSize) {
	const response = await api.get(
		`/doctors/?page=${pageIndex}&size=${pageSize}`,
	);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function useDoctors(pageIndex, pageSize) {
	return useQuery({
		queryKey: ["doctors", pageIndex, pageSize],
		queryFn: () => fetchDoctors(pageIndex, pageSize),
	});
}
