import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchDoctorAppointments(
	id,
	pageIndex,
	pageSize,
	startDateTime = undefined,
	endDateTime = undefined,
) {
	const startTimeQuery = startDateTime ? `&start=${startDateTime}` : "";
	const endTimeQuery = endDateTime ? `&end=${endDateTime}` : "";

	const response = await api.get(
		`/appointments/by-doctor/${id}?page=${pageIndex}&size=${pageSize}${startTimeQuery}${endTimeQuery}`,
	);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function useDoctorAppointments(
	id,
	pageIndex,
	pageSize,
	startDateTime = undefined,
	endDateTime = undefined,
) {
	return useQuery({
		queryKey: [
			"doctor-appointments",
			id,
			pageIndex,
			pageSize,
			startDateTime,
			endDateTime,
		],
		queryFn: () =>
			fetchDoctorAppointments(
				id,
				pageIndex,
				pageSize,
				startDateTime,
				endDateTime,
			),
	});
}
