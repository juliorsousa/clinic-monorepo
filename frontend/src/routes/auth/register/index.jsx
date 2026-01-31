import { createFileRoute, redirect } from "@tanstack/react-router";
import { RegisterForm } from "./-components/register-form";

export const Route = createFileRoute("/auth/register/")({
	component: RegisterForm,
	head: () => ({
		meta: [
			{
				title: "Cadastro | Clínica",
			},
			{
				name: "auth:description",
				content:
					"Referência em cuidado integral à saúde, oferecendo atendimento humanizado, ético e de qualidade.",
			},
			{
				name: "auth:subtitle",
				content: "Cadastre-se para continuar",
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
