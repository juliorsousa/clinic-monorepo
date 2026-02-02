import { ThemeProvider } from "@/components/theme/theme-provider";
import { Toaster } from "@/components/ui/sonner";
import { QueryClientProvider } from "@tanstack/react-query";
import { RouterProvider, createRouter } from "@tanstack/react-router";
import { AuthProvider } from "./contexts/auth-provider";
import { OnboardingProvider } from "./contexts/onboarding-provider";
import { ReprofilingProvider } from "./contexts/reprofiling-provider";
import { useAuth } from "./hooks/use-auth";
import { useOnboarding } from "./hooks/use-onboarding";
import { useReprofiling } from "./hooks/use-reprofiling";
import { queryClient } from "./lib/query-client";
import { routeTree } from "./routeTree.gen";

function InnerApp() {
	const auth = useAuth();
	const onboarding = useOnboarding();
	const reprofiling = useReprofiling();

	const router = createRouter({
		routeTree,
		context: {
			auth,
			onboarding,
			reprofiling,
		},
	});

	return <RouterProvider router={router} />;
}

export function App() {
	return (
		<ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
			<QueryClientProvider client={queryClient}>
				<AuthProvider>
					<OnboardingProvider>
						<ReprofilingProvider>
							<InnerApp />
							<Toaster richColors />
						</ReprofilingProvider>
					</OnboardingProvider>
				</AuthProvider>
			</QueryClientProvider>
		</ThemeProvider>
	);
}
