import { useQuery } from "@tanstack/react-query";
import { api } from "../api";

export async function fetchSummarizedDoctor(id) {
	const response = await api.get(`/doctors/${id}/summary`);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function useSummarizedDoctor(id) {
	return useQuery({
		queryKey: ["summarized-doctor", id],
		queryFn: () => fetchSummarizedDoctor(id),
	});
}

export async function fetchSummarizedDoctorsBySpecialty(specialty) {
	if (!specialty) {
		return [];
	}

	const response = await api.get(`/doctors/by-specialty/${specialty}/summary`);

	if (!response.data) {
		throw new Error("No response data.");
	}

	return response.data;
}

export function useSummarizedDoctorsBySpecialty(specialty) {
	return useQuery({
		queryKey: ["summarized-doctors", specialty],
		queryFn: () => fetchSummarizedDoctorsBySpecialty(specialty),
	});
}
