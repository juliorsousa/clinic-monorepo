import { api } from "@/lib/api";
import { createContext, useEffect, useState } from "react";

import Cookies from "js-cookie";

export const AuthContext = createContext({});

export function AuthProvider({ children }) {
	const [isAuthLoading, setIsAuthLoading] = useState(true);
	const [alreadyRefreshed, setAlreadyRefreshed] = useState(false);
	const [user, setUser] = useState(null);
	const [isAuthenticated, setIsAuthenticated] = useState(false);

	useEffect(() => {
		const accessToken = Cookies.get("access_token");

		if (accessToken) {
			api
				.get("/users/me", {
					headers: {
						Authorization: `Bearer ${Cookies.get("access_token")}`,
					},
				})
				.then((response) => {
					setIsAuthLoading(false);
					setIsAuthenticated(true);
					setUser(response.data);
				})
				.catch(() => {
					setIsAuthLoading(false);
					setIsAuthenticated(false);
					setUser(null);

					signOut();
				});
		}
	}, []);

	async function revalidate() {
		try {
			const response = await api.get("/users/me", {
				headers: {
					Authorization: `Bearer ${Cookies.get("access_token")}`,
				},
			});

			if (response.status === 200) {
				setIsAuthLoading(false);
				setIsAuthenticated(true);
				setUser(response.data);
			} else throw new Error("Failed to revalidate user.");
		} catch {
			signOut();
		}
	}

	function hasTrait(trait) {
		return user?.traits?.includes(trait);
	}

	function signOut() {
		setUser(null);
		setIsAuthenticated(false);

		if (!Cookies.get("access_token")) return;

		api.post("/auth/log-out").finally(() => {
			Cookies.remove("access_token");
		});
	}

	async function signIn({ email, password }) {
		const response = await api.post("/auth/login", {
			email,
			password,
		});

		if (response.status !== 200) {
			throw new Error("Usuário ou senha inválidos.");
		}

		const data = response.data;

		if (data.accessToken == null) {
			throw new Error("Usuário ou senha inválidos.");
		}

		const { accessToken } = response.data;

		Cookies.set("access_token", accessToken, {
			expires: new Date(Date.now() + 15 * 60 * 1000),
			sameSite: "strict",
		});

		const redirectTo = "/";

		window.location.replace(redirectTo);
	}

	async function register({ email, password }) {
		const response = await api.post("/auth/register", {
			email,
			password,
		});

		if (response.status !== 200 && response.status !== 201) {
			throw new Error("Erro ao criar conta.");
		}

		const data = response.data;

		if (!data?.token) {
			throw new Error("Erro ao criar conta.");
		}

		const { token } = data;

		Cookies.set("access_token", token.accessToken, {
			expires: new Date(Date.now() + 15 * 60 * 1000),
			sameSite: "strict",
		});

		window.location.replace("/");
	}

	// function refresh() {
	// 	if (alreadyRefreshed || redirectUri === "") return;

	// 	const refreshToken = getCookie("refresh_token");

	// 	if (!refreshToken) {
	// 		signOut();
	// 		return;
	// 	}

	// 	oauth
	// 		.post("/refresh", {
	// 			grant_type: "refresh_token",
	// 			refresh_token: refreshToken,
	// 		})
	// 		.then((response) => {
	// 			const { access_token, refresh_token } = response.data;

	// 			setCookie("access_token", access_token, {
	// 				expires: new Date(Date.now() + 15 * 60 * 1000),
	// 				sameSite: "strict",
	// 			});

	// 			setCookie("refresh_token", refresh_token, {
	// 				expires: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
	// 				sameSite: "strict",
	// 			});

	// 			setIsAuthenticated(true);
	// 			setIsAuthLoading(false);
	// 			setAlreadyRefreshed(true);

	// 			const decoded = decodeURIComponent(redirectUri);

	// 			let target = decoded + "?access_token=" + access_token;

	// 			const returnTo = searchParams.get("return");

	// 			if (returnTo) {
	// 				target += "&return=" + encodeURIComponent(returnTo);
	// 			}

	// 			const currentUrl = window.location.pathname;

	// 			if (isValidUrlToRedirect(currentUrl)) {
	// 				router.replace(target);
	// 			}
	// 		})
	// 		.catch(() => signOut());
	// }

	return (
		<AuthContext.Provider
			value={{
				signIn,
				isAuthLoading,
				isAuthenticated,
				user,
				register,
				hasTrait,
				revalidate,
			}}
		>
			{children}
		</AuthContext.Provider>
	);
}
