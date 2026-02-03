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
		} else {
			setIsAuthLoading(false);
			setIsAuthenticated(false);
			setUser(null);
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

	function hasRole(role) {
		return user?.roles?.find((r) => r.role === role) != null;
	}

	function hasPendingIntentFor(role) {
		return user?.pendingIntents?.find((r) => r.type === role) != null;
	}

	function hasAnyPendingIntent() {
		return user?.pendingIntents?.length > 0;
	}

	function signOut() {
		setUser(null);
		setIsAuthenticated(false);
		setIsAuthLoading(false);

		if (!Cookies.get("access_token")) return;

		api.post("/auth/log-out").finally(() => {
			Cookies.remove("access_token");
		});
	}

	function getSpecificRoleId(role) {
		return (
			user?.roles?.find((r) => r.role === role)?.referencedEntityId || null
		);
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
			expires: new Date(Date.now() + 60 * 60 * 1000),
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
			expires: new Date(Date.now() + 60 * 60 * 1000),
			sameSite: "strict",
		});

		window.location.replace("/");
	}

	return (
		<AuthContext.Provider
			value={{
				signIn,
				isAuthLoading,
				isAuthenticated,
				user,
				register,
				hasTrait,
				hasRole,
				revalidate,
				signOut,
				getSpecificRoleId,
				hasPendingIntentFor,
				hasAnyPendingIntent,
			}}
		>
			{children}
		</AuthContext.Provider>
	);
}
