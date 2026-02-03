export function formatDate(date) {
	const pad = (n) => n.toString().padStart(2, "0");

	const day = pad(date.getDate());
	const month = pad(date.getMonth() + 1);
	const year = date.getFullYear();

	const hours = pad(date.getHours());
	const minutes = pad(date.getMinutes());

	return `${day}/${month}/${year} ${hours}:${minutes}`;
}

export function formatDateToHour(date) {
	const pad = (n) => n.toString().padStart(2, "0");

	const hours = pad(date.getHours());
	const minutes = pad(date.getMinutes());

	return `${hours}:${minutes}`;
}
