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

		if (auth.hasTrait("MUST_CHANGE_PASSWORD")) {
			throw redirect({ to: "/auth/password" });
		}

		if (auth.hasTrait("AWAITING_INTENT_APPROVAL")) {
			throw redirect({ to: "/onboarding/done/pending" });
		}

		if (auth.hasTrait("AWAITING_PROFILE_CREATION")) {
			throw redirect({ to: "/onboarding/done/handling" });
		}
	},
});

function AppLayout() {
	return <Outlet />;
}
