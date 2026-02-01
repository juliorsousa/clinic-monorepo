import { unmaskPostalCode } from "./cep-utils";
import { unmaskCPF, validateCPF } from "./cpf-utils";
import { unmaskPhone } from "./phone-utils";

export function hasSelectedProfile(data) {
	return data.profile?.value === "DOCTOR" || data.profile?.value === "PATIENT";
}

export function validateCustomerData(raw) {
	const errors = [];

	const data = raw.personal || {};

	console.log("Validating customer data:", data);

	if (!data.personal?.name || data.personal.name.trim().length === 0) {
		errors.push("O nome completo é obrigatório.");
	} else if (data.personal.name.length > 100) {
		errors.push("O nome completo deve ter no máximo 100 caracteres.");
	}

	const document = unmaskCPF(data.personal?.document || "");
	if (!document) {
		errors.push("O documento é obrigatório.");
	} else if (document.length > 20) {
		errors.push("O documento deve ter no máximo 20 caracteres.");
	} else if (!validateCPF(document)) {
		errors.push("CPF inválido.");
	}

	const phone = unmaskPhone(data.personal?.phone || "");
	if (!phone) {
		errors.push("O telefone é obrigatório.");
	} else if (!/^\d{2}9?\d{8}$/.test(phone)) {
		errors.push("Telefone inválido.");
	} else if (phone.length > 15) {
		errors.push("O telefone deve ter no máximo 15 caracteres.");
	}

	if (!data.address?.street || data.address.street.trim().length === 0) {
		errors.push("A rua é obrigatória.");
	} else if (data.address.street.length > 100) {
		errors.push("A rua deve ter no máximo 100 caracteres.");
	}

	if (!data.address?.house || data.address.house.trim().length === 0) {
		errors.push("O número da casa é obrigatório.");
	} else if (data.address.house.length > 10) {
		errors.push("O número da casa deve ter no máximo 10 caracteres.");
	}

	if (data.address?.complement && data.address.complement.length > 50) {
		errors.push("O complemento deve ter no máximo 50 caracteres.");
	}

	if (
		!data.address?.neighborhood ||
		data.address.neighborhood.trim().length === 0
	) {
		errors.push("O bairro é obrigatório.");
	} else if (data.address.neighborhood.length > 50) {
		errors.push("O bairro deve ter no máximo 50 caracteres.");
	}

	if (!data.address?.city || data.address.city.trim().length === 0) {
		errors.push("A cidade é obrigatória.");
	} else if (data.address.city.length > 50) {
		errors.push("A cidade deve ter no máximo 50 caracteres.");
	}

	if (!data.address?.state || data.address.state.trim().length === 0) {
		errors.push("O estado é obrigatório.");
	}

	const zip = data.address?.zipCode || "";
	if (!zip) {
		errors.push("O CEP é obrigatório.");
	} else if (!/^\d{8}$/.test(unmaskPostalCode(zip))) {
		errors.push("CEP inválido.");
	}

	console.log("[customer] Validation errors:", errors);

	return errors;
}

const validSpecialties = [
	"DERMATOLOGY",
	"ORTHOPEDICS",
	"CARDIOLOGY",
	"GYNECOLOGY",
];

export function validateDoctorSpecificData(raw) {
	if (raw.profile?.value !== "DOCTOR") {
		return [];
	}

	const data = raw.specific || {};

	console.log("Validating DOCTOR specific data:", data, raw);

	const errors = [];

	if (!data.credential || data.credential.trim().length === 0) {
		errors.push("A credencial é obrigatória.");
	} else if (
		!/^CRM?\s*[-\/]?\s*\d{4,7}(\s*[-\/]\s*[A-Z]{2})?$|^CRM\s*[-\/]?\s*[A-Z]{2}\s+\d{4,7}$/.test(
			data.credential,
		)
	) {
		errors.push("O formato da credencial é inválido.");
	}

	if (!data.specialty || data.specialty.trim().length === 0) {
		errors.push("A especialidade é obrigatória.");
	}

	if (!validSpecialties.includes(data.specialty)) {
		errors.push("A especialidade é inválida.");
	}

	console.log("[DOCTOR] Validation errors:", errors);

	return errors;
}
