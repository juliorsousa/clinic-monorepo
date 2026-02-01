export function maskPhone(value) {
	const digits = value.replace(/\D/g, "");

	if (digits.length <= 2) {
		return digits;
	}

	if (digits.length <= 6) {
		return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
	}

	if (digits.length <= 10) {
		return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
	}

	if (digits.length <= 11) {
		return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`;
	}

	if (digits[2] === "9") {
		return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
	}

	return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6, 10)}`;
}

export function unmaskPhone(value) {
	return value.replace(/\D/g, "");
}
