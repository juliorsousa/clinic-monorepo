export function maskPostalCode(value) {
	return value
		.replace(/\D/g, "")
		.replace(/(\d{5})(\d)/, "$1-$2")
		.replace(/(-\d{3})\d+?$/, "$1");
}

export function unmaskPostalCode(value) {
	return value.replace(/\D/g, "");
}
