import { createFileRoute } from "@tanstack/react-router";
import { AuthenticationForm } from "./-components/authentication-form";

export const Route = createFileRoute("/_app/auth/login/")({
	component: AuthenticationForm,
	head: () => ({
		meta: [
			{
				title: "Autentique-se | Clínica",
			},
		],
	}),
});
