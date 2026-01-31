import { Outlet, createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/_app")({
	component: AppLayout,
	beforeLoad: ({ context }) => {
		const { auth } = context;

		console.log("Auth beforeLoad:", {
			isAuthLoading: auth.isAuthLoading,
			isAuthenticated: auth.isAuthenticated,
			user: auth.user,
		});

		if (!auth.isAuthenticated) {
			throw redirect({ to: "/auth/login" });
		}

		if (auth.user?.traits?.includes("MUST_CHANGE_PASSWORD")) {
			throw redirect({ to: "/auth/password" });
		}
	},
});

function AppLayout() {
	return <Outlet />;
}
