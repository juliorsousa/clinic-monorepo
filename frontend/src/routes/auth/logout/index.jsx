import { Loading } from "@/components/loading";
import { useAuth } from "@/hooks/use-auth";
import { createFileRoute, redirect, useNavigate } from "@tanstack/react-router";
import { useEffect } from "react";

export const Route = createFileRoute("/auth/logout/")({
	component: LogoutPage,
	head: () => ({
		meta: [
			{
				title: "Saindo... | Clínica",
			},
			{
				name: "auth:title",
				content: "Aguarde enquanto estamos invalidando a sua sessão.",
			},
			{
				name: "auth:subtitle",
				content: "Você está saindo...",
			},
		],
	}),
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (auth.isAuthLoading) return;

		if (!auth.isAuthenticated) {
			throw redirect({ to: "/auth/login" });
		}
	},
});

function LogoutPage() {
	const navigate = useNavigate();
	const { signOut } = useAuth();

	useEffect(() => {
		const logout = async () => {
			await signOut();
			navigate({ to: "/auth/login" });
		};

		logout();
	}, [signOut, navigate]);

	return (
		<div className="flex flex-1 items-center justify-center p-4">
			<Loading />
		</div>
	);
}
