import { OnboardingRouterBridge } from "@/bridges/onboarding-router-bridge";
import { Loading } from "@/components/loading";
import { NotFound } from "@/routes/404";
import {
	HeadContent,
	Outlet,
	createRootRouteWithContext,
	redirect,
} from "@tanstack/react-router";
import { TanStackRouterDevtools } from "@tanstack/react-router-devtools";
import { LoadingPage } from "./loading";

export const Route = createRootRouteWithContext()({
	component: () => (
		<>
			<HeadContent />
			<OnboardingRouterBridge />
			<Outlet />
			<TanStackRouterDevtools position="top-right" />
		</>
	),
	notFoundComponent: () => <NotFound />,
	beforeLoad: ({ context, location }) => {
		const { auth, onboarding } = context;
		const { isAuthLoading, hasTrait } = auth;
		const { isOnboardingLoading } = onboarding;

		if (isAuthLoading || isOnboardingLoading) {
			return <LoadingPage />;
		}

		const isOnboardingRoute = location.pathname.startsWith("/onboarding");
		const isOnboardingDoneRoute =
			location.pathname.startsWith("/onboarding/done");

		if (isOnboardingDoneRoute) {
			if (
				hasTrait("AWAITING_PROFILE_CREATION") ||
				hasTrait("AWAITING_INTENT_APPROVAL")
			) {
				return <Outlet />;
			}
		}

		if (context.onboarding.isOnboardingEligible && !isOnboardingRoute) {
			throw redirect({ to: "/onboarding" });
		}

		if (!context.onboarding.isOnboardingEligible && isOnboardingRoute) {
			throw redirect({ to: "/" });
		}
	},
});
