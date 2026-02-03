import { OnboardingRouterBridge } from "@/bridges/onboarding-router-bridge";
import { ReprofilingRouterBridge } from "@/bridges/reprofiling-router-bridge";
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
			<ReprofilingRouterBridge />
			<Outlet />
			<TanStackRouterDevtools position="top-right" />
		</>
	),
	notFoundComponent: () => <NotFound />,
	beforeLoad: ({ context, location }) => {
		const { auth, onboarding, reprofiling } = context;
		const { isAuthLoading, hasTrait } = auth;
		const { isOnboardingLoading } = onboarding;
		const { isReprofilingLoading } = reprofiling;

		if (isAuthLoading || isOnboardingLoading || isReprofilingLoading) {
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
				console.log("User is on onboarding done route");
				return <Outlet />;
			}
		}

		if (onboarding.isOnboardingEligible && !isOnboardingRoute) {
			console.log("Redirecting to onboarding from non-onboarding route");
			throw redirect({ to: "/onboarding" });
		}

		if (!onboarding.isOnboardingEligible && isOnboardingRoute) {
			console.log("Redirecting to home from onboarding route");
			throw redirect({ to: "/" });
		}

		const isReprofilingRoute = location.pathname.startsWith("/reprofiling");
		const isReprofilingDoneRoute =
			location.pathname.startsWith("/reprofiling/done");

		if (isReprofilingRoute && auth.hasRole("ADMIN")) {
			throw redirect({ to: "/" });
		}

		if (
			auth.hasPendingIntentFor("PATIENT") &&
			isReprofilingRoute &&
			!isReprofilingDoneRoute
		) {
			throw redirect({ to: "/reprofiling/done/handling" });
		}

		if (
			auth.hasPendingIntentFor("DOCTOR") &&
			isReprofilingRoute &&
			!isReprofilingDoneRoute
		) {
			throw redirect({ to: "/reprofiling/done/pending" });
		}
	},
});
