export function maskCPF(value) {
	return value
		.replace(/\D/g, "")
		.replace(/(\d{3})(\d)/, "$1.$2")
		.replace(/(\d{3})(\d)/, "$1.$2")
		.replace(/(\d{3})(\d{1,2})$/, "$1-$2")
		.slice(0, 14);
}

export function unmaskCPF(value) {
	return value.replace(/\D/g, "");
}

export function validateCPF(originalCpf) {
	if (!originalCpf) return false;

	let cpf = originalCpf;

	cpf = cpf.replace(/\D/g, "");

	if (cpf.length !== 11) return false;

	let sum = 0;
	let remainder;

	for (let i = 1; i <= 9; i++) {
		sum += Number.parseInt(cpf.charAt(i - 1)) * (11 - i);
	}
	remainder = (sum * 10) % 11;

	if (remainder === 10 || remainder === 11) {
		remainder = 0;
	}
	if (remainder !== Number.parseInt(cpf.charAt(9))) {
		return false;
	}

	sum = 0;
	for (let i = 1; i <= 10; i++) {
		sum += Number.parseInt(cpf.charAt(i - 1)) * (12 - i);
	}
	remainder = (sum * 10) % 11;

	if (remainder === 10 || remainder === 11) {
		remainder = 0;
	}
	if (remainder !== Number.parseInt(cpf.charAt(10))) {
		return false;
	}

	return true;
}
