import { createFileRoute, redirect } from "@tanstack/react-router";
import { AuthenticationForm } from "./-components/authentication-form";

export const Route = createFileRoute("/auth/login/")({
	component: AuthenticationForm,
	head: () => ({
		meta: [
			{
				title: "Autentique-se | Clínica",
			},
			{ name: "auth:title", content: "Clínica IFB-A" },
			{
				name: "auth:description",
				content:
					"Referência em cuidado integral à saúde, oferecendo atendimento humanizado, ético e de qualidade.",
			},
			{
				name: "auth:subtitle",
				content: "Acesse sua conta para continuar",
			},
		],
	}),
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (auth.isAuthLoading) return;

		if (auth.isAuthenticated) {
			throw redirect({ to: "/" });
		}
	},
});
