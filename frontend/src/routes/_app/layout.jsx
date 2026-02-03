import { AppSidebar } from "@/components/app-sidebar";
import { SidebarInset, SidebarProvider } from "@/components/ui/sidebar";
import { useAuth } from "@/hooks/use-auth";
import { Outlet, createFileRoute, redirect } from "@tanstack/react-router";
import { LoadingPage } from "../loading";

export const Route = createFileRoute("/_app")({
	component: AppLayout,
	beforeLoad: ({ context }) => {
		const { auth } = context;

		if (auth.isAuthLoading) {
			return <LoadingPage />;
		}

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
	const { isAuthLoading } = useAuth();

	return (
		<>
			{isAuthLoading ? (
				<LoadingPage />
			) : (
				<SidebarProvider>
					<AppSidebar />
					<SidebarInset>
						<Outlet />
					</SidebarInset>
				</SidebarProvider>
			)}
		</>
	);
}
