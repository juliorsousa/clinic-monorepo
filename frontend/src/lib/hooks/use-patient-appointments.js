import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchPatientAppointments(id, pageIndex, pageSize) {
	const response = await api.get(
		`/appointments/by-patient/${id}?page=${pageIndex}&size=${pageSize}`,
	);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function usePatientAppointments(id, pageIndex, pageSize) {
	return useQuery({
		queryKey: ["patient-appointments", id, pageIndex, pageSize],
		queryFn: () => fetchPatientAppointments(id, pageIndex, pageSize),
	});
}
