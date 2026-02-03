import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchDoctorsAvailabilityDates(ids) {
	if (!Array.isArray(ids) || ids.length === 0) {
		return [];
	}

	const response = await api.post(
		"/appointments/availability/dates",
		Array.isArray(ids) ? ids : [ids],
	);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function useDoctorsAvailabilityDates(ids) {
	return useQuery({
		queryKey: ["doctors-availability-dates", ids],
		queryFn: () => fetchDoctorsAvailabilityDates(ids),
	});
}

export async function fetchDoctorsAvailabilityHours(date, ids) {
	if (!date || !Array.isArray(ids) || ids.length === 0) {
		return [];
	}

	const response = await api.post(
		`/appointments/availability/hours/${date}`,
		Array.isArray(ids) ? ids : [ids],
	);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function useDoctorsAvailabilityHours(date, ids) {
	return useQuery({
		queryKey: ["doctors-availability-hours", date, ids],
		queryFn: () => fetchDoctorsAvailabilityHours(date, ids),
	});
}
