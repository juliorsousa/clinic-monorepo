import { createFileRoute, redirect } from "@tanstack/react-router";
import { ChangePasswordForm } from "./-components/change-password-form";

export const Route = createFileRoute("/auth/password/")({
	component: ChangePasswordForm,
	head: () => ({
		meta: [
			{
				title: "Redefinição de Senha | Clínica",
			},
			{ name: "auth:title", content: "Clínica IFB-A" },
			{
				name: "auth:description",
				content:
					"Para garantir a segurança da sua conta, é necessário redefinir sua senha.",
			},
			{
				name: "auth:subtitle",
				content: "Redefina sua senha para continuar",
			},
		],
	}),
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (
			!auth.isAuthenticated ||
			!auth.user.traits.includes("MUST_CHANGE_PASSWORD")
		) {
			throw redirect({ to: "/auth/login" });
		}
	},
});
