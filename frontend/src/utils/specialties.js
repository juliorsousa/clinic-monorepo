export const medicalSpecialties = [
	["ORTHOPEDICS", "Ortopedia"],
	["CARDIOLOGY", "Cardiologia"],
	["GYNECOLOGY", "Ginecologia"],
	["DERMATOLOGY", "Dermatologia"],
];

export function getSpecialtyLabel(value) {
	const specialty = medicalSpecialties.find(([key]) => key === value);

	return specialty ? specialty[1] : null;
}
