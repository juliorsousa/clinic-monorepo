export const profileTypeMap = {
	PATIENT: "Paciente",
	DOCTOR: "Médico",
};

export const specialtyLabelMap = {
	DERMATOLOGY: "Dermatologia",
	GYNECOLOGY: "Ginecologia",
	CARDIOLOGY: "Cardiologia",
	ORTHOPEDICS: "Ortopedia",
};

export const specialtyClassNamesMap = {
	DERMATOLOGY:
		"border-transparent bg-amber-500 text-amber-50 [a&]:hover:bg-amber-600",
	GYNECOLOGY:
		"border-transparent bg-pink-500 text-pink-50 [a&]:hover:bg-pink-600",
	CARDIOLOGY: "border-transparent bg-red-500 text-red-50 [a&]:hover:bg-red-600",
	ORTHOPEDICS:
		"border-transparent bg-gray-500 text-gray-50 [a&]:hover:bg-gray-600",
};

export const appointmentStatusLabelMap = {
	SCHEDULED: "Agendada",
	CONFIRMED: "Confirmada",
	CANCELLED: "Cancelada",
	ONGOING: "Em Andamento",
	COMPLETED: "Concluída",
};

export const appointmentStatusClassNamesMap = {
	SCHEDULED:
		"border-transparent bg-gray-500 text-gray-50 [a&]:hover:bg-gray-600",
	CONFIRMED:
		"border-transparent bg-blue-500 text-blue-50 [a&]:hover:bg-blue-600",
	CANCELLED: "border-transparent bg-red-500 text-red-50 [a&]:hover:bg-red-600",
	ONGOING:
		"border-transparent bg-yellow-500 text-yellow-50 [a&]:hover:bg-yellow-600",
	COMPLETED:
		"border-transparent bg-green-500 text-green-50 [a&]:hover:bg-green-600",
};

export const intentStatusLabelMap = {
	PENDING: "Pendente",
	PROCESSED: "Processado",
	ERRORED: "Com Erro",
	REJECTED: "Rejeitado",
	APPROVED: "Aprovado",
	IMPLICIT: "Implícito",
};

export const intentStatusClassNamesMap = {
	PENDING: "border-transparent bg-gray-500 text-gray-50 [a&]:hover:bg-gray-600",
	PROCESSED:
		"border-transparent bg-blue-500 text-blue-50 [a&]:hover:bg-blue-600",
	ERRORED:
		"border-transparent bg-amber-600 text-amber-50 [a&]:hover:bg-amber-600",
	REJECTED: "border-transparent bg-red-500 text-red-50 [a&]:hover:bg-red-600",
	APPROVED:
		"border-transparent bg-green-500 text-green-50 [a&]:hover:bg-green-600",
	IMPLICIT:
		"border-transparent bg-purple-500 text-purple-50 [a&]:hover:bg-purple-600",
};
