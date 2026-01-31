import axios from "axios";
import Cookies from "js-cookie";

export const api = createApiClient();

// let isRefreshing = false
// let failedRequestsQueue = []

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

					redirectToSSO();

					return Promise.reject(error);
				}

				if (error.response.data?.error === "Access token expired.") {
					const originalConfig = error.config;

					// if (!isRefreshing) {
					//   isRefreshing = true

					//   axios
					//     .get(`gt`, {
					//       withCredentials: true,
					//     })
					//     .then((response) => {
					//       const { accessToken } = response.data

					//       if (!accessToken) {
					//         localStorage.removeItem('staily.accessToken')

					//         redirectToSSO()

					//         return Promise.reject(error)
					//       }

					//       localStorage.setItem('staily.accessToken', accessToken)

					//       api.defaults.headers.Authorization = `Bearer ${accessToken}`

					//       failedRequestsQueue.forEach((request) =>
					//         request.onSuccess(accessToken),
					//       )
					//       failedRequestsQueue = []
					//     })
					//     .catch((err) => {
					//       failedRequestsQueue.forEach((request) => request.onFailure(err))
					//       failedRequestsQueue = []

					//       localStorage.removeItem('staily.accessToken')

					//       redirectToSSO()
					//     })
					//     .finally(() => {
					//       isRefreshing = false
					//     })
					// }

					throw new Error("Failed to refresh token");

					// return new Promise((resolve, reject) => {
					//   failedRequestsQueue.push({
					//     onSuccess: (token) => {
					//       originalConfig.headers.Authorization = `Bearer ${token}`

					//       resolve(api(originalConfig))
					//     },
					//     onFailure: (err) => {
					//       reject(err)
					//     },
					//   })
					// })
				}
			}

			return Promise.reject(error);
		},
	);

	return api;
}

function redirectToLogin() {
	window.location.replace("/auth/login");
}
