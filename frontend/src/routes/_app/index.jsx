import { Outlet, createFileRoute, redirect } from "@tanstack/react-router";
import { LoadingPage } from "../loading";

export const Route = createFileRoute("/_app/")({
	component: Outlet,
	head: () => ({
		meta: [
			{
				title: "Página Inicial | Clínica",
			},
		],
	}),
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (auth.isAuthLoading) {
			return <LoadingPage />;
		}

		if (auth.hasRole("ADMIN")) {
			throw redirect({ to: "/patients" });
		}

		if (auth.hasRole("DOCTOR")) {
			throw redirect({ to: "/doctors/me" });
		}

		if (auth.hasRole("PATIENT")) {
			throw redirect({ to: "/patients/me" });
		}
	},
});
