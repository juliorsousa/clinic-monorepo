import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

/*
	{
			"page": 0,
			"totalPages": 1,
			"totalElements": 5,
			"content": [
					{
							"id": "cde2d8ff-b54e-486e-9447-759f6a2f6380",
							"patientId": "7f000101-9c20-1632-819c-20f957d50005",
							"doctorId": "7f000101-9c21-1147-819c-21a588bf0000",
							"status": "CONFIRMED",
							"observation": "Completed appointment",
							"scheduledTo": "2026-02-03T02:05:18.886261"
					},
					{
							"id": "f794cbdb-24b1-44a2-a03c-d0a73a744d0e",
							"patientId": "7f000101-9c20-1632-819c-20f957d50005",
							"doctorId": "7f000101-9c21-1147-819c-21a588bf0000",
							"status": "CONFIRMED",
							"observation": "Ongoing appointment",
							"scheduledTo": "2026-02-03T03:35:18.886261"
					},
					{
							"id": "4c928f72-9b5a-4a18-9147-35bf83f05d8b",
							"patientId": "7f000101-9c20-1632-819c-20f957d50005",
							"doctorId": "7f000101-9c21-1dbc-819c-219ece520000",
							"status": "CONFIRMED",
							"observation": "Confirmed appointment",
							"scheduledTo": "2026-02-03T16:05:18.886261"
					},
					{
							"id": "dda1cd6f-7766-4307-929e-3bd228513bc2",
							"patientId": "7f000101-9c20-1632-819c-20f957d50005",
							"doctorId": "7f000101-9c21-1dbc-819c-219ece520000",
							"status": "CANCELLED",
							"observation": "Cancelled appointment",
							"scheduledTo": "2026-02-05T04:05:18.886261"
					},
					{
							"id": "fcb427d5-d83f-4aef-8890-d3527799fc1e",
							"patientId": "7f000101-9c20-1632-819c-20f957d50005",
							"doctorId": "7f000101-9c21-1dbc-819c-219ece520000",
							"status": "SCHEDULED",
							"observation": "Scheduled appointment",
							"scheduledTo": "2026-02-06T04:05:18.886261"
					}
			]
	}
*/

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
