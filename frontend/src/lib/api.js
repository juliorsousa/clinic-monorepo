import axios from "axios";
import Cookies from "js-cookie";

export const api = createApiClient();

function createApiClient(baseURL = import.meta.env.VITE_API_URL) {
	const accessToken = Cookies.get("access_token");
	const api = axios.create({
		baseURL,
		headers: {
			Authorization: accessToken ? `Bearer ${accessToken}` : "",
		},
	});

	api.interceptors.response.use(
		(response) => {
			return response;
		},
		(error) => {
			console.error("API error:", error);
			if (error.response.status === 401) {
				if (!Cookies.get("access_token")) {
					Cookies.remove("access_token");

					redirectToLogout();

					return Promise.reject(error);
				}

				if (error.response.data?.error === "Access token expired.") {
					redirectToLogout();
				}
			}

			return Promise.reject(error);
		},
	);

	return api;
}

function redirectToLogout() {
	window.location.replace("/auth/logout");
}
